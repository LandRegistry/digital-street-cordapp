package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.common.utils.FlowLogicCommonMethods
import com.hmlr.contracts.LandAgreementContract
import com.hmlr.contracts.LandTitleContract
import com.hmlr.contracts.ProposedChargeAndRestrictionContract
import com.hmlr.model.ActionOnRestriction
import com.hmlr.model.AgreementStatus
import com.hmlr.model.DTCConsentStatus
import com.hmlr.model.LandTitleStatus
import com.hmlr.states.LandAgreementState
import com.hmlr.states.LandTitleState
import com.hmlr.states.ProposedChargesAndRestrictionsState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.toBase58String


/**
 * Initiating flow to transfer land title to buyer on completion date.
 * The flow is triggered after the sales agreement has reached [AgreementStatus.COMPLETED]
 */
@InitiatingFlow
@SchedulableFlow
class InitiateLandTitleTransferFlow(val ref: StateRef): FlowLogic<Unit>(), FlowLogicCommonMethods{

    companion object {
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating Land title transfer transaction")
        object FETCHING_LAND_TITLE_STATE : ProgressTracker.Step("Fetching the Land title state")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with node private key")
        object COLLECTING_SIGNATURE_FROM_COUNTERPARTY: ProgressTracker.Step("Collecting signature from counter party") {
            override fun childProgressTracker() = CollectSignaturesFlow.tracker()
        }
        object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        private fun tracker() = ProgressTracker(
                GENERATING_TRANSACTION,
                FETCHING_LAND_TITLE_STATE,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                COLLECTING_SIGNATURE_FROM_COUNTERPARTY,
                FINALISING_TRANSACTION
        )
    }

    override val progressTracker = tracker()

    /**
     * The flows logic is encapsulated within the call() method.
     */
    @Suspendable
    override fun call() {
        val notary = serviceHub.firstNotary()
        val agreementStateAndRef = serviceHub.loadState(stateRef = ref)
        val agreementState = agreementStateAndRef.data as LandAgreementState
        if (agreementState.status != AgreementStatus.COMPLETED) {
            throw FlowException("Cannot initiate transfer of ownership")
        } else {
            // Role decider: to determine which party initiates the land title transfer flow. It shouldn't really matter which party initiates that.
            // Contract checks for the signature from both the parties
            val parties = agreementState.participants.sortedBy { it.owningKey.toBase58String() }
            if (ourIdentity.owningKey == parties[0].owningKey) {
                progressTracker.currentStep = GENERATING_TRANSACTION
                val tx = TransactionBuilder(notary = notary)

                progressTracker.currentStep = FETCHING_LAND_TITLE_STATE
                val landTitleStateAndRef = serviceHub.loadState(UniqueIdentifier.fromString(agreementState.titleStateLinearId), LandTitleState::class.java)
                val chargeAndRestrictionStateAndRef = serviceHub.loadState(UniqueIdentifier.fromString(landTitleStateAndRef.state.data.proposedChargeOrRestrictionLinearId), ProposedChargesAndRestrictionsState::class.java)
                val landTitleState = landTitleStateAndRef.state.data
                val chargeAndRestrictionState = chargeAndRestrictionStateAndRef.state.data

                val newRestrictionStatus = serviceHub.updateRestrictions(chargeAndRestrictionState.restrictions, ActionOnRestriction.NO_ACTION,false)
                val newOwner = agreementState.buyer
                val newLandTitleOutputState = landTitleState.copy(status = LandTitleStatus.TRANSFERRED, landTitleProperties = landTitleState.landTitleProperties.copy(ownerConveyancer = agreementState.buyerConveyancer, owner = newOwner, ownerLender = chargeAndRestrictionState.buyerLender!!), lastSoldValue = agreementState.purchasePrice, restrictions = newRestrictionStatus, participants = listOf(agreementState.buyerConveyancer, landTitleState.titleIssuer, chargeAndRestrictionState.buyerLender!!, agreementState.sellerConveyancer, landTitleState.landTitleProperties.ownerLender), charges = chargeAndRestrictionState.charges)
                val newAgreementState = agreementState.copy(status = AgreementStatus.TRANSFERRED)
                val newChargeAndRestrictionState = chargeAndRestrictionState.copy(ownerConveyancer = chargeAndRestrictionState.buyerConveyancer!!, buyerConveyancer = null, buyerLender = null, status = DTCConsentStatus.ISSUED, restrictions = newRestrictionStatus, participants = listOf(agreementState.buyerConveyancer, landTitleState.titleIssuer, chargeAndRestrictionState.buyerLender!!, agreementState.sellerConveyancer, landTitleState.landTitleProperties.ownerLender))

                // add input states
                tx.addInputState(landTitleStateAndRef)
                tx.addInputState(StateAndRef(agreementStateAndRef, ref))
                tx.addInputState(chargeAndRestrictionStateAndRef)

                // add output states
                tx.addOutputState(newAgreementState, LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID)
                tx.addOutputState(newLandTitleOutputState, LandTitleContract.LAND_TITLE_CONTRACT_ID)
                tx.addOutputState(newChargeAndRestrictionState, ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID)

                // add commands
                val landTransferCommand = Command(LandTitleContract.Commands.TransferLandTitle(), listOf(agreementState.buyerConveyancer.owningKey, agreementState.sellerConveyancer.owningKey, landTitleState.titleIssuer.owningKey))
                val agreementFinalizeCommand = Command(LandAgreementContract.Commands.FinalizeAgreement(), listOf(landTitleState.landTitleProperties.ownerConveyancer.owningKey, agreementState.buyerConveyancer.owningKey, landTitleState.titleIssuer.owningKey))
                val issueChargeRestriction = Command(ProposedChargeAndRestrictionContract.Commands.TransferLandTitle() ,listOf(agreementState.buyerConveyancer.owningKey, agreementState.sellerConveyancer.owningKey, landTitleState.titleIssuer.owningKey))

                tx.addCommand(landTransferCommand)
                tx.addCommand(agreementFinalizeCommand)
                tx.addCommand(issueChargeRestriction)

                progressTracker.currentStep = VERIFYING_TRANSACTION
                tx.verify(serviceHub)

                progressTracker.currentStep = SIGNING_TRANSACTION
                val partSignedTx = serviceHub.signInitialTransaction(tx)

                progressTracker.currentStep = COLLECTING_SIGNATURE_FROM_COUNTERPARTY
                val counterparty = serviceHub.identityService.wellKnownPartyFromAnonymous(parties[1])
                        ?: throw IllegalStateException("Cannot resolve responding party")
                val counterPartySession = initiateFlow(counterparty)
                val titleIssuerPartySession = initiateFlow(newLandTitleOutputState.titleIssuer)
                val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, setOf(counterPartySession, titleIssuerPartySession), COLLECTING_SIGNATURE_FROM_COUNTERPARTY.childProgressTracker()))

                progressTracker.currentStep = FINALISING_TRANSACTION
                subFlow(FinalityFlow(fullySignedTx, FINALISING_TRANSACTION.childProgressTracker()))
            }
        }
    }
}
