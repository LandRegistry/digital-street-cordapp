package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.contracts.LandAgreementContract
import com.hmlr.model.AgreementStatus
import com.hmlr.states.LandAgreementState
import net.corda.core.contracts.Command
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

@InitiatingFlow
@StartableByRPC
class SellerSignAgreement(val agreementStateLinearId: String,
                          val sellerSignature: ByteArray): FlowLogic<SignedTransaction>() {

    companion object {
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction .")
        object FETCHING_AGREEMENT_STATE : ProgressTracker.Step("Fetching the Agreement state .")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints.")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with our private key.")
        object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
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

    @Suspendable
    override fun call(): SignedTransaction {
        // obtain a reference to the notary
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        // STEP: 1
        progressTracker.currentStep = GENERATING_TRANSACTION
        val tx = TransactionBuilder(notary = notary)

        // STEP: 2
        progressTracker.currentStep = FETCHING_AGREEMENT_STATE
        val agreementStateAndRef = getAgreementState(serviceHub, agreementStateLinearId)

        // add input state
        tx.addInputState(agreementStateAndRef)

        // add output states
        val outputAgreementState = agreementStateAndRef.state.data as LandAgreementState
        val newOutputAgreementState = outputAgreementState.copy(status = AgreementStatus.SIGNED, seller = outputAgreementState.seller.copy(signature = sellerSignature))
        tx.addOutputState(newOutputAgreementState, LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID)

        // add commands
        val sellerSignCommand = Command(LandAgreementContract.Commands.SellerSignAgreement(), outputAgreementState.sellerConveyancer.owningKey)
        tx.addCommand(sellerSignCommand)

        // STEP: 3
        progressTracker.currentStep = VERIFYING_TRANSACTION
        tx.verify(serviceHub)

        // STEP: 4
        progressTracker.currentStep = SIGNING_TRANSACTION
        val signedTx = serviceHub.signInitialTransaction(tx)

        // STEP: 5
        progressTracker.currentStep = FINALISING_TRANSACTION
        return subFlow(FinalityFlow(signedTx))
    }

    @Suspendable
    private fun getAgreementState(serviceHub: ServiceHub, linearId: String): StateAndRef<*> {
        val generalCriteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED)
        val customCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(UniqueIdentifier.fromString(linearId)))
        val criteria = generalCriteria.and(customCriteria)
        val results = serviceHub.vaultService.queryBy<LinearState>(criteria = criteria).states
        return results.single()
    }
}