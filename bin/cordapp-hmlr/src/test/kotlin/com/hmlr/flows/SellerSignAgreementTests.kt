package com.hmlr.flows

import com.hmlr.model.AgreementStatus
import com.hmlr.states.LandAgreementState
import net.corda.core.contracts.TransactionVerificationException
import org.junit.Test
import kotlin.test.assertFailsWith

class SellerSignAgreementTests:  LandTitleTests() {

    @Test
    fun `seller signs agreement state successfully`() {
        sellerSignAgreement(sellerConveyancer, sellerPrivateKey)
        mockNetwork.waitQuiescent()

        // check land agreement state on buyer conveyancer node
        buyerConveyancer.transaction {
            val landAgreementStates = buyerConveyancer.services.vaultService.queryBy(LandAgreementState::class.java).states
            assert(landAgreementStates.size == 1)
            assert(landAgreementStates[0].state.data.status == AgreementStatus.SIGNED)
        }

        // check land agreement state on seller conveyancer node
        sellerConveyancer.transaction {
            val landAgreementStates = sellerConveyancer.services.vaultService.queryBy(LandAgreementState::class.java).states
            assert(landAgreementStates.size == 1)
            assert(landAgreementStates[0].state.data.status == AgreementStatus.SIGNED)
        }
    }

    @Test
    fun `only the land owner can sign the agreement`() {
        assertFailsWith<TransactionVerificationException> {  sellerSignAgreement(sellerConveyancer, wrongUser.private) }
    }
}