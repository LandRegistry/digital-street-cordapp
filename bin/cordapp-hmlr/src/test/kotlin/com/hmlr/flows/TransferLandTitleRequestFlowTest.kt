package com.hmlr.flows

import com.hmlr.model.Address
import com.hmlr.model.CustomParty
import com.hmlr.model.Status
import com.hmlr.model.UserType
import com.hmlr.states.LandTitleState
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.core.singleIdentity
import org.jgroups.util.Util
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TransferLandTitleRequestFlowTest: LandTitleTests() {

    @Test
    fun `successfully send land transfer request`() {
        val recordedTx = landTitleTransferRequest()!!.getOrThrow()
        val landTitleState = recordedTx.tx.outputStates[0] as LandTitleState
        Util.assertEquals(landTitleState.status, Status.PENDING_BUYER_APPROVAL)
    }

    @Test
    fun `Transfer title request flow cannot be started by issuer`() {
        // Transfer land title request
        val location = Address("A1-S2", "GreenBank Road", "Devon", "PL6", "UK", "PL6 5ZD")
        val buyingParty = CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nitesh@example.com", "9876543210", true, publicKey = buyerPublicKey, signature = null)
        val acceptingParty = buyerConveyancer.info.singleIdentity()
        requestForIssuance()
        mockNetwork.waitQuiescent()
        sellerConveyancer.transaction {
            val txOutputs = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            val recordedState = txOutputs[0].state.data
            val transferFlow = TransferLandTitleRequest(recordedState.linearId.toString(), buyingParty, acceptingParty, sign(recordedState.titleID, sellerPrivateKey))
            val future = issuer.startFlow(transferFlow)
            mockNetwork.runNetwork()
            assertFailsWith<SignedTransaction.SignaturesMissingException> { future!!.getOrThrow() }
        }
    }

    @Test
    fun `flow records a transaction in both parties transaction storages`() {
        val future = landTitleTransferRequest()
        val recordedTx = future!!.getOrThrow()
        for (node in listOf(sellerConveyancer, buyerConveyancer, issuer)) {
            assertEquals(recordedTx, node.services.validatedTransactions.getTransaction(recordedTx.id))
        }
    }
}