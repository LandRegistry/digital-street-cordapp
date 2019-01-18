package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.common.utils.FlowLogicCommonMethods
import com.hmlr.contracts.InstructConveyancerContract
import com.hmlr.contracts.RequestIssuanceContract
import com.hmlr.states.InstructConveyancerState
import com.hmlr.states.RequestIssuanceState
import net.corda.core.contracts.Command
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

/**
 * Initiating flow to request for issuance of land title.
 * This will be called by the instructed conveyancer to request Land Registry for issuing the land title
 */
@InitiatingFlow
@StartableByRPC
class RequestIssuanceFlow(val requestIssuanceState: RequestIssuanceState,
                          val instructionStateLinearId: String) : FlowLogic<SignedTransaction>(), FlowLogicCommonMethods {

    companion object {
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating Request Issuance transaction")
        object FETCHING_INSTRUCTION_STATE : ProgressTracker.Step("Fetching the Instruction state")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with node private key")
        object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }
        object SENDING_TRANSACTION_ID_TO_ISSUER : ProgressTracker.Step("Sending transaction id to land title issuer")

        fun tracker() = ProgressTracker(
                GENERATING_TRANSACTION,
                FETCHING_INSTRUCTION_STATE,
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
       val notary = serviceHub.firstNotary()

        // STEP: 1
        progressTracker.currentStep = GENERATING_TRANSACTION
        val tx = TransactionBuilder(notary = notary)

        // STEP: 2
        progressTracker.currentStep = FETCHING_INSTRUCTION_STATE
        val instructionStateAndRef = serviceHub.loadState(UniqueIdentifier.fromString(instructionStateLinearId), InstructConveyancerState::class.java)
        tx.addInputState(instructionStateAndRef)

        // Add output state
        tx.addOutputState(requestIssuanceState, RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID)

        // Add RequestIssuance Command
        val requestIssuanceCommand = Command(RequestIssuanceContract.Commands.RequestIssuance(), listOf(requestIssuanceState.sellerConveyancer.owningKey))
        tx.addCommand(requestIssuanceCommand)

        // Add AcceptInstruction Command
        val acceptInstructionCommand = Command(InstructConveyancerContract.Commands.AcceptInstruction(), listOf(requestIssuanceState.sellerConveyancer.owningKey))
        tx.addCommand(acceptInstructionCommand)

        // STEP: 2
        progressTracker.currentStep = VERIFYING_TRANSACTION
        tx.verify(serviceHub)

        // STEP: 3
        progressTracker.currentStep = SIGNING_TRANSACTION
        val signedTx = serviceHub.signInitialTransaction(tx)

        // STEP: 4
        progressTracker.currentStep = FINALISING_TRANSACTION
        val finalizedTx = subFlow(FinalityFlow(signedTx, FINALISING_TRANSACTION.childProgressTracker()))

        // STEP: 5
        progressTracker.currentStep = SENDING_TRANSACTION_ID_TO_ISSUER
        // initiate session with issuer
        val issuerSession = initiateFlow(requestIssuanceState.titleIssuer)
        issuerSession.send(finalizedTx.tx.id)
        return finalizedTx
    }
}