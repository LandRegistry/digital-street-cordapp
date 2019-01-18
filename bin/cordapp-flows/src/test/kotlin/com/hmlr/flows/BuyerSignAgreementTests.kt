package com.hmlr.flows

import com.hmlr.model.AgreementStatus
import com.hmlr.states.LandAgreementState
import net.corda.core.contracts.TransactionVerificationException
import org.junit.Test
import kotlin.test.assertFailsWith

class BuyerSignAgreementTests : AbstractFlowTestUtils() {

    @Test
    fun `buyer signs agreement state successfully`() {
        val tx = buyerSignAgreement(buyerConveyancer, buyerPrivateKey)
        mockNetwork.waitQuiescent()

        // check land agreement state on buyer conveyancer node
        buyerConveyancer.transaction {
            val landAgreementStates = tx.tx.outputs[0].data as LandAgreementState
            assert(tx.tx.outputs.size == 1)
            assert(landAgreementStates.status == AgreementStatus.COMPLETED)
        }

        // check land agreement state on seller conveyancer node
        sellerConveyancer.transaction {
            val landAgreementStates = tx.tx.outputs[0].data as LandAgreementState
            assert(tx.tx.outputs.size == 1)
            assert(landAgreementStates.status == AgreementStatus.COMPLETED)
        }
    }

    @Test
    fun `only the intended buyer can sign the agreement`() {
        assertFailsWith<TransactionVerificationException> { buyerSignAgreement(buyerConveyancer, wrongUser.private) }
    }
}
