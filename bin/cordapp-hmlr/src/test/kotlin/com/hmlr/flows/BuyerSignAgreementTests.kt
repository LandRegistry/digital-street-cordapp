package com.hmlr.flows

import com.hmlr.model.AgreementStatus
import com.hmlr.states.LandAgreementState
import net.corda.core.contracts.TransactionVerificationException
import org.junit.Test
import kotlin.test.assertFailsWith

class BuyerSignAgreementTests: LandTitleTests() {

    @Test
    fun `buyer signs agreement state successfully`() {
        buyerSignAgreement(buyerConveyancer, buyerPrivateKey)
        mockNetwork.waitQuiescent()

        // check land agreement state on buyer conveyancer node
        buyerConveyancer.transaction {
            val landAgreementStates = buyerConveyancer.services.vaultService.queryBy(LandAgreementState::class.java).states
            assert(landAgreementStates.size == 1)
            assert(landAgreementStates[0].state.data.status == AgreementStatus.COMPLETED)
        }

        // check land agreement state on seller conveyancer node
        sellerConveyancer.transaction {
            val landAgreementStates = sellerConveyancer.services.vaultService.queryBy(LandAgreementState::class.java).states
            assert(landAgreementStates.size == 1)
            assert(landAgreementStates[0].state.data.status == AgreementStatus.COMPLETED)
        }
    }

    @Test
    fun `only the intended buyer can sign the agreement`() {
        assertFailsWith<TransactionVerificationException> { buyerSignAgreement(buyerConveyancer, wrongUser.private) }
    }
}
