package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.common.utils.FlowLogicCommonMethods
import com.hmlr.contracts.LandAgreementContract
import com.hmlr.model.AgreementStatus
import com.hmlr.states.LandAgreementState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

/**
 * Initiating flow for buyer to sign the agreement
 */
@InitiatingFlow
@StartableByRPC
class BuyerSignAgreementFlow(val agreementStateLinearId: String,
                         val buyerSignature: ByteArray): FlowLogic<SignedTransaction>(), FlowLogicCommonMethods {

    companion object {
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction")
        object FETCHING_AGREEMENT_STATE : ProgressTracker.Step("Fetching the Agreement state")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with node private key")
        object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(
                GENERATING_TRANSACTION,
                FETCHING_AGREEMENT_STATE,
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
        progressTracker.currentStep = FETCHING_AGREEMENT_STATE
        val agreementStateAndRef = serviceHub.loadState(UniqueIdentifier.fromString(agreementStateLinearId), LandAgreementState::class.java)

        // add input state
        tx.addInputState(agreementStateAndRef)

        // add output states
        val outputAgreementState = agreementStateAndRef.state.data
        val newOutputAgreementState = outputAgreementState.copy(status = AgreementStatus.COMPLETED, buyer = outputAgreementState.buyer.copy(signature = buyerSignature))
        tx.addOutputState(newOutputAgreementState, LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID)
        // add commands
        val buyerSignCommand = Command(LandAgreementContract.Commands.BuyerSignAgreement(), outputAgreementState.buyerConveyancer.owningKey)
        tx.addCommand(buyerSignCommand)

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