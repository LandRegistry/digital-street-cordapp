package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.contracts.RequestIssuanceContract
import com.hmlr.states.RequestIssuanceState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

/**
 * Initiating flow to request for issuance of land title.
 *
 */
@InitiatingFlow
@StartableByRPC
class RequestIssuanceFlow(val requestIssuanceState: RequestIssuanceState) : FlowLogic<SignedTransaction>() {

    companion object {
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating Request Issuance transaction .")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints.")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with our private key.")
        object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }
        object SENDING_TRANSACTION_ID_TO_ISSUER : ProgressTracker.Step("Sending transaction id to issuer")

        fun tracker() = ProgressTracker(
                GENERATING_TRANSACTION,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                FINALISING_TRANSACTION,
                SENDING_TRANSACTION_ID_TO_ISSUER
        )
    }

    override val progressTracker = tracker()

    /**
     * The flows logic is encapsulated within the call() method.
     */
    @Suspendable
    override fun call(): SignedTransaction {

        // obtain a reference to the notary
       val notary = serviceHub.networkMapCache.notaryIdentities[0]

        // STEP: 1
        progressTracker.currentStep = GENERATING_TRANSACTION
        val tx = TransactionBuilder(notary = notary)

        // Add output state
        tx.addOutputState(requestIssuanceState, RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID)

        // Add Command
        val command = Command(RequestIssuanceContract.Commands.RequestIssuance(), listOf(requestIssuanceState.sellerConveyancer.owningKey))
        tx.addCommand(command)

        // STEP: 2
        progressTracker.currentStep = VERIFYING_TRANSACTION
        tx.verify(serviceHub)

        // STEP: 3
        progressTracker.currentStep = SIGNING_TRANSACTION
        val signedTx = serviceHub.signInitialTransaction(tx)

        // STEP: 4
        progressTracker.currentStep = FINALISING_TRANSACTION
        val finalizedTx = subFlow(FinalityFlow(signedTx))

        // STEP: 5
        progressTracker.currentStep = SENDING_TRANSACTION_ID_TO_ISSUER
        // initiate session with issuer
        val issuerSession = initiateFlow(requestIssuanceState.titleIssuer)
        issuerSession.send(finalizedTx.tx.id)
        return finalizedTx
    }
}