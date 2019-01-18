package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.contracts.LandAgreementContract
import com.hmlr.model.AgreementStatus
import com.hmlr.states.LandAgreementState
import net.corda.core.contracts.Command
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.lang.IllegalArgumentException


/**
 * This is a sub-flow which is executed on a Conveyancer node and it will approve a Land Agreement on the Corda ledger.
 */
@InitiatingFlow
@StartableByRPC
class ApproveAgreement(val agreementStateLinearId: String) : FlowLogic<SignedTransaction>() {

    companion object {
        object GENERATING_TRANSACTION: ProgressTracker.Step("Generating Approve Agreement transaction")
        object FETCHING_AGREEMENT_STATE: ProgressTracker.Step("Fetching agreement state using linear ID")
        object VERIFYING_TRANSACTION: ProgressTracker.Step("Generating Approve Agreement transaction")
        object SIGNING_TRANSACTION: ProgressTracker.Step("Signing Approve Agreement transaction")
        object FINALISING_APPROVE_AGREEMENT_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
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

    override val progressTracker = ApproveAgreement.tracker()

    @Suspendable
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        progressTracker.currentStep = GENERATING_TRANSACTION
        val tx = TransactionBuilder(notary = notary)

        progressTracker.currentStep = FETCHING_AGREEMENT_STATE
        val landAgreementStateAndRef = getAgreementState(serviceHub, agreementStateLinearId)

        tx.addInputState(landAgreementStateAndRef)

        val landAgreementOutputState = landAgreementStateAndRef.state.data as LandAgreementState
        val newLandAgreementOutputState = landAgreementOutputState.copy(status = AgreementStatus.APPROVED)

        tx.addOutputState(newLandAgreementOutputState, LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID)

        val approveAgreementCommand = Command(LandAgreementContract.Commands.ApproveSalesAgreement(), landAgreementOutputState.buyerConveyancer.owningKey)
        tx.addCommand(approveAgreementCommand)

        progressTracker.currentStep = VERIFYING_TRANSACTION
        tx.verify(serviceHub)

        progressTracker.currentStep = SIGNING_TRANSACTION
        val signedTx = serviceHub.signInitialTransaction(tx)

        progressTracker.currentStep = FINALISING_APPROVE_AGREEMENT_TRANSACTION
        return subFlow(FinalityFlow(signedTx))
    }

    @Suspendable
    @Throws(IllegalArgumentException::class, NoSuchElementException::class)
    private fun getAgreementState(serviceHub: ServiceHub, linearId: String): StateAndRef<*> {
        val generalCriteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED)
        val customCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(UniqueIdentifier.fromString(linearId)))
        val criteria = generalCriteria.and(customCriteria)
        val results = serviceHub.vaultService.queryBy<LinearState>(criteria).states
        return results.single()
    }
}

