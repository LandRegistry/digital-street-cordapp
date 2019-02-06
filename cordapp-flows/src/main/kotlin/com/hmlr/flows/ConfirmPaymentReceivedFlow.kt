package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.common.utils.FlowLogicCommonMethods
import com.hmlr.contracts.PaymentConfirmationContract
import com.hmlr.model.PaymentConfirmationStatus
import com.hmlr.states.PaymentConfirmationState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

/**
 * Initiating flow for settling party to confirm payment received  in their escrow account
 */
@InitiatingFlow
@StartableByRPC
class ConfirmPaymentReceivedFlow(val paymentConfirmationStateLinearId: String) : FlowLogic<SignedTransaction>(), FlowLogicCommonMethods {

    companion object {
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction")
        object FETCHING_PAYMENT_CONFIRMATION_STATE : ProgressTracker.Step("Fetching the PaymentConfirmation state")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with node private key")
        object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(
                GENERATING_TRANSACTION,
                FETCHING_PAYMENT_CONFIRMATION_STATE,
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
        progressTracker.currentStep = ConfirmPaymentReceivedFlow.Companion.GENERATING_TRANSACTION
        val tx = TransactionBuilder(notary = notary)

        // STEP: 2
        progressTracker.currentStep = ConfirmPaymentReceivedFlow.Companion.FETCHING_PAYMENT_CONFIRMATION_STATE
        val paymentConfirmationStateAndRef = serviceHub.loadState(UniqueIdentifier.fromString(paymentConfirmationStateLinearId), PaymentConfirmationState::class.java)

        // add input state
        tx.addInputState(paymentConfirmationStateAndRef)

        // add output states
        val paymentConfirmationOutputState = paymentConfirmationStateAndRef.state.data
        val newPaymentConfirmationOutputState = paymentConfirmationOutputState.copy(status = PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW)
        tx.addOutputState(newPaymentConfirmationOutputState, PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID)

        // add commands
        val confirmPaymentReceivedCommand = Command(PaymentConfirmationContract.Commands.ConfirmPaymentReceived(), newPaymentConfirmationOutputState.settlingParty.owningKey)
        tx.addCommand(confirmPaymentReceivedCommand)

        // STEP: 3
        progressTracker.currentStep = ConfirmPaymentReceivedFlow.Companion.VERIFYING_TRANSACTION
        tx.verify(serviceHub)

        // STEP: 4
        progressTracker.currentStep = ConfirmPaymentReceivedFlow.Companion.SIGNING_TRANSACTION
        val signedTx = serviceHub.signInitialTransaction(tx)

        // STEP: 5
        progressTracker.currentStep = ConfirmPaymentReceivedFlow.Companion.FINALISING_TRANSACTION
        return subFlow(FinalityFlow(signedTx, ConfirmPaymentReceivedFlow.Companion.FINALISING_TRANSACTION.childProgressTracker()))
    }
}