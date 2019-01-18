package com.hmlr.flows


import com.hmlr.states.LandTitleState
import com.hmlr.model.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import org.junit.Test
import kotlin.test.assertFailsWith
import org.jgroups.util.Util.assertEquals

class TransferLandTitleResponseFlowTest: LandTitleTests() {

    @Test
    fun `successfully accept land transfer request`() {
        val transferTx = landTitleTransferResponse()!!.getOrThrow()
        val landTitleState = transferTx.tx.outputStates[0] as LandTitleState
        assertEquals(landTitleState.status, Status.TRANSFERRED)
    }

    @Test
    fun `Transfer title response flow cannot be started by issuer`() {
        requestForIssuance()
        landTitleTransferRequest()
        mockNetwork.waitQuiescent()
        return buyerConveyancer.transaction {
            val txOutputs = buyerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            val recordedState = txOutputs[0].state.data
            val responseFlow = TransferLandTitleResponse(recordedState.linearId.toString(), sign(recordedState.titleID, buyerPrivateKey))
            val future = issuer.startFlow(responseFlow)
            mockNetwork.runNetwork()
            assertFailsWith<SignedTransaction.SignaturesMissingException> { future.getOrThrow() }
        }
    }

    @Test
    fun `Flow records a transaction in both parties' transaction storages`() {
        val future = landTitleTransferResponse()
        val recordedTx = future!!.getOrThrow()
        for (node in listOf(sellerConveyancer, buyerConveyancer, issuer)) {
            kotlin.test.assertEquals(recordedTx, node.services.validatedTransactions.getTransaction(recordedTx.id))
        }
    }
}