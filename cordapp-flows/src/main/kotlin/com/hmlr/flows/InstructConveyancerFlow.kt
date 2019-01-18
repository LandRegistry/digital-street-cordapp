package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.common.utils.FlowLogicCommonMethods
import com.hmlr.contracts.InstructConveyancerContract
import com.hmlr.states.InstructConveyancerState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

/**
 * Initiating flow to instruct a conveyancer for a particular land title, allowing them to request for issuance of a land title.
 */
@InitiatingFlow
@StartableByRPC
class InstructConveyancerFlow(val instructConveyancerState: InstructConveyancerState) : FlowLogic<SignedTransaction>(), FlowLogicCommonMethods {

    companion object {
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating Instruct Conveyancer transaction")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with node private key")
        object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(
                GENERATING_TRANSACTION,
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

        // Add output state
        tx.addOutputState(instructConveyancerState, InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID)

        // Add Command
        val command = Command(InstructConveyancerContract.Commands.CreateInstruction(), listOf(instructConveyancerState.titleIssuer.owningKey))
        tx.addCommand(command)

        // STEP: 2
        progressTracker.currentStep = VERIFYING_TRANSACTION
        tx.verify(serviceHub)

        // STEP: 3
        progressTracker.currentStep = SIGNING_TRANSACTION
        val signedTx = serviceHub.signInitialTransaction(tx)

        // STEP: 4
        progressTracker.currentStep = FINALISING_TRANSACTION
        return subFlow(FinalityFlow(signedTx, FINALISING_TRANSACTION.childProgressTracker()))
    }

}
