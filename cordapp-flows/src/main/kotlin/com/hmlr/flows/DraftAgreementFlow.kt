package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.common.utils.FlowLogicCommonMethods
import com.hmlr.contracts.LandAgreementContract
import com.hmlr.contracts.LandTitleContract
import com.hmlr.contracts.ProposedChargeAndRestrictionContract
import com.hmlr.model.DTCConsentStatus
import com.hmlr.model.LandTitleStatus
import com.hmlr.schema.LandTitleStateSchemaV1
import com.hmlr.states.LandAgreementState
import com.hmlr.states.LandTitleState
import com.hmlr.states.ProposedChargesAndRestrictionsState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.seconds

/**
 * Initiating flow to draft sales agreement on the ledger.
 */
@InitiatingFlow
@StartableByRPC
class DraftAgreementFlow(val agreementState: LandAgreementState,
                     val respondingConveyancer: Party): FlowLogic<SignedTransaction>(), FlowLogicCommonMethods {

    companion object {
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating Draft Agreement transaction")
        object FETCH_LAND_TITLE_STATE : ProgressTracker.Step("Fetching the land title state")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with node private key")
        object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(
                GENERATING_TRANSACTION,
                FETCH_LAND_TITLE_STATE,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                FINALISING_TRANSACTION
        )
    }

    override val progressTracker = tracker()

    /**
     * The flows logic is encapsulated within the call() method.
     */
    @Suspendable
    override fun call(): SignedTransaction {

        // obtain a reference to the notary
        val notary = serviceHub.firstNotary()

        // STEP: 1
        progressTracker.currentStep = GENERATING_TRANSACTION
        val tx = TransactionBuilder(notary = notary)

        // STEP: 2
        progressTracker.currentStep = FETCH_LAND_TITLE_STATE
        val landTitleStateAndRef = serviceHub.getLandTitleAssetByTitleId(agreementState.titleID)
        val chargeAndRestrictionStateAndRef = serviceHub.loadState(UniqueIdentifier.fromString(landTitleStateAndRef.state.data.proposedChargeOrRestrictionLinearId), ProposedChargesAndRestrictionsState::class.java)

        // add input state
        tx.addInputState(landTitleStateAndRef)
        tx.addInputState(chargeAndRestrictionStateAndRef)

        // add output states
        val landTitleOutputState = landTitleStateAndRef.state.data
        val chargeAndRestrictionOutputState = chargeAndRestrictionStateAndRef.state.data

        val newLandTitleOutputState = landTitleOutputState.copy(participants = landTitleOutputState.participants + respondingConveyancer, status = LandTitleStatus.ASSIGN_BUYER_CONVEYANCER)
        val newChargeAndRestrictionOutputState = chargeAndRestrictionOutputState.copy(participants = chargeAndRestrictionOutputState.participants + respondingConveyancer, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, buyerConveyancer = respondingConveyancer)

        tx.addOutputState(newLandTitleOutputState, LandTitleContract.LAND_TITLE_CONTRACT_ID)
        tx.addOutputState(agreementState, LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID)
        tx.addOutputState(newChargeAndRestrictionOutputState, ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID)

        // add commands
        val landTitleStateData = landTitleStateAndRef.state.data
        val assignBuyerConveyancerCommand = Command(LandTitleContract.Commands.AssignBuyerConveyancer(), landTitleStateData.landTitleProperties.ownerConveyancer.owningKey)
        val createDraftAgreementCommand = Command(LandAgreementContract.Commands.CreateDraftAgreement(), landTitleStateData.landTitleProperties.ownerConveyancer.owningKey)
        val assignBuyerConveyancer = Command(ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer() ,listOf(agreementState.sellerConveyancer.owningKey))


        tx.addCommand(assignBuyerConveyancerCommand)
        tx.addCommand(createDraftAgreementCommand)
        tx.addCommand(assignBuyerConveyancer)

        tx.setTimeWindow(serviceHub.clock.instant(), 60.seconds)

        // STEP: 3
        progressTracker.currentStep = VERIFYING_TRANSACTION
        tx.verify(serviceHub)

        // STEP: 4
        progressTracker.currentStep = SIGNING_TRANSACTION
        val signedTx = serviceHub.signInitialTransaction(tx)

        // STEP: 5
        progressTracker.currentStep = FINALISING_TRANSACTION
        return subFlow(FinalityFlow(signedTx, FINALISING_TRANSACTION.childProgressTracker()))
    }
}