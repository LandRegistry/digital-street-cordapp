package com.hmlr.flows


import com.hmlr.model.AgreementStatus
import com.hmlr.states.LandAgreementState
import net.corda.core.transactions.SignedTransaction
import org.junit.Test
import kotlin.test.assertFailsWith

class LandAgreementApproveTests : AbstractFlowTestUtils() {

    @Test
    fun `approve draft agreement successfully`() {
        approveLandAgreement(buyerConveyancer)
        mockNetwork.waitQuiescent()

        // check land agreement state on buyer conveyancer node
        buyerConveyancer.transaction {
            val landAgreementStates = buyerConveyancer.services.vaultService.queryBy(LandAgreementState::class.java).states
            assert(landAgreementStates.size == 1)
            assert(landAgreementStates[0].state.data.status == AgreementStatus.APPROVED)
        }

        // check land agreement state on seller conveyancer node
        sellerConveyancer.transaction {
            val landAgreementStates = sellerConveyancer.services.vaultService.queryBy(LandAgreementState::class.java).states
            assert(landAgreementStates.size == 1)
            assert(landAgreementStates[0].state.data.status == AgreementStatus.APPROVED)
        }
    }

    @Test
    fun `draft agreement should only be approved by buyer's conveyancer`() {
        assertFailsWith<SignedTransaction.SignaturesMissingException> { approveLandAgreement(sellerConveyancer) }
    }

}