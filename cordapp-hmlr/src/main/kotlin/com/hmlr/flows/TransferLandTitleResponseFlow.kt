package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.contracts.LandTitleContract
import com.hmlr.contracts.LandTitleContract.Companion.LAND_TITLE_CONTRACT_ID
import com.hmlr.states.LandTitleState
import com.hmlr.model.*
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step


/**
 * This flows allows Seller to create the Land Title transfer response.
 */
@InitiatingFlow
@StartableByRPC
class TransferLandTitleResponse(val titleID: String,
                                val buyerSignature: ByteArray) : FlowLogic<SignedTransaction>() {

    companion object {
        object GENERATING_TRANSACTION : Step("Generating transaction based on Title state.")
        object VERIFYING_TRANSACTION : Step("Verifying contract constraints.")
        object SIGNING_TRANSACTION : Step("Signing transaction with our private key.")
        object FINALISING_TRANSACTION : Step("Obtaining notary signature and recording transaction.") {
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
        // Obtain a reference to the notary we want to use.
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        // Stage 1.
        progressTracker.currentStep = GENERATING_TRANSACTION
        // Generate an unsigned transaction.
        val inputTitleState = serviceHub.vaultService.queryBy<LandTitleState>().states.singleOrNull { it.state.data.linearId.toString() == titleID } ?: throw FlowException("No state found in the vault")
        val titleLinerId = inputTitleState.state.data.linearId.copy(id = inputTitleState.state.data.linearId.id)
        val newTitleProperties = inputTitleState.state.data.landTitleProperties.copy(ownerConveyancer = inputTitleState.state.data.landTitleProperties.buyerConveyancer, owner = inputTitleState.state.data.landTitleProperties.buyer!!.copy(signature = buyerSignature), buyer = null, buyerConveyancer = null)
        val outputTitleState = inputTitleState.state.data.copy(linearId = titleLinerId, landTitleProperties = newTitleProperties, status = Status.TRANSFERRED)

        val txCommand = Command(LandTitleContract.Commands.LandTitleTransferResponse(), inputTitleState.state.data.landTitleProperties.buyerConveyancer!!.owningKey)
        val txBuilder = TransactionBuilder(notary)
                .addOutputState(outputTitleState, LAND_TITLE_CONTRACT_ID)
                .addInputState(inputTitleState)
                .addCommand(txCommand)

        // Stage 2.
        progressTracker.currentStep = VERIFYING_TRANSACTION
        // Verify that the transaction is valid.
        txBuilder.verify(serviceHub)

        // Stage 3.
        progressTracker.currentStep = SIGNING_TRANSACTION
        // Sign the transaction.
        val partSignedTx = serviceHub.signInitialTransaction(txBuilder)

        // Stage 4.
        progressTracker.currentStep = FINALISING_TRANSACTION
        // Notarise and record the transaction in both parties' vaults.
        return subFlow(FinalityFlow(partSignedTx, FINALISING_TRANSACTION.childProgressTracker()))
    }
}

