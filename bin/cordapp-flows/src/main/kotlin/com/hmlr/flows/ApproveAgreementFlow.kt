package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.common.utils.FlowLogicCommonMethods
import com.hmlr.contracts.LandAgreementContract
import com.hmlr.model.AgreementStatus
import com.hmlr.states.LandAgreementState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker


/**
 * Initiating flow to approve sales agreement.
 */
@InitiatingFlow
@StartableByRPC
class ApproveAgreementFlow(val agreementStateLinearId: String) : FlowLogic<SignedTransaction>(), FlowLogicCommonMethods {

    companion object {
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating Approve Agreement transaction")
        object FETCHING_AGREEMENT_STATE : ProgressTracker.Step("Fetching agreement state using linear ID")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing Approve Agreement transaction with node private key")
        object FINALISING_APPROVE_AGREEMENT_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(
                GENERATING_TRANSACTION,
                FETCHING_AGREEMENT_STATE,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                FINALISING_APPROVE_AGREEMENT_TRANSACTION
        )
    }

    override val progressTracker = ApproveAgreementFlow.tracker()

    /**
     * The flows logic is encapsulated within the call() method.
     */
    @Suspendable
    override fun call(): SignedTransaction {
        val notary = serviceHub.firstNotary()

        progressTracker.currentStep = GENERATING_TRANSACTION
        val tx = TransactionBuilder(notary = notary)

        progressTracker.currentStep = FETCHING_AGREEMENT_STATE
        val landAgreementStateAndRef = serviceHub.loadState(UniqueIdentifier.fromString(agreementStateLinearId), LandAgreementState::class.java)

        tx.addInputState(landAgreementStateAndRef)

        val landAgreementOutputState = landAgreementStateAndRef.state.data
        val newLandAgreementOutputState = landAgreementOutputState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true)

        tx.addOutputState(newLandAgreementOutputState, LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID)

        val approveAgreementCommand = Command(LandAgreementContract.Commands.ApproveSalesAgreement(), landAgreementOutputState.buyerConveyancer.owningKey)
        tx.addCommand(approveAgreementCommand)

        progressTracker.currentStep = VERIFYING_TRANSACTION
        tx.verify(serviceHub)

        progressTracker.currentStep = SIGNING_TRANSACTION
        val signedTx = serviceHub.signInitialTransaction(tx)

        progressTracker.currentStep = FINALISING_APPROVE_AGREEMENT_TRANSACTION
        return subFlow(FinalityFlow(signedTx, FINALISING_APPROVE_AGREEMENT_TRANSACTION.childProgressTracker()))
    }
}

