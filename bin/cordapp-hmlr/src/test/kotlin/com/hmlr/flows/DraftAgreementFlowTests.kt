package com.hmlr.flows

import com.hmlr.model.AgreementStatus
import com.hmlr.model.LandTitleStatus
import com.hmlr.states.LandAgreementState
import com.hmlr.states.LandTitleState
import org.junit.Test
import kotlin.test.assertFailsWith

class DraftAgreementFlowTests: LandTitleTests() {

    @Test
    fun `create draft agreement successfully`(){
        createDraftAgreement(sellerConveyancer)
        mockNetwork.waitQuiescent()

        // check for agreement state and land title state on seller conveyancer node
        sellerConveyancer.transaction {
            val agreementState = sellerConveyancer.services.vaultService.queryBy(LandAgreementState::class.java).states
            assert(agreementState.size == 1)
            assert(agreementState[0].state.data.status == AgreementStatus.CREATED)

            val landTitleState = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(landTitleState.size == 1)
            assert(landTitleState[0].state.data.status == LandTitleStatus.ASSIGN_BUYER_CONVEYANCER)
        }

        // fetch the titleID of the land title state from buyer conveyancer
        buyerConveyancer.transaction {
            // check for agreement state and land title state on seller conveyancer node
            val agreementState = buyerConveyancer.services.vaultService.queryBy(LandAgreementState::class.java).states
            assert(agreementState.size == 1)
            assert(agreementState[0].state.data.status == AgreementStatus.CREATED)

            val landTitleState = buyerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(landTitleState.size == 1)
            assert(landTitleState[0].state.data.status == LandTitleStatus.ASSIGN_BUYER_CONVEYANCER)
        }
    }

    @Test
    fun `draft agreement should only be created by the seller's conveyancer`() {
        // create draft agreement with wrong seller
        assertFailsWith<NoSuchElementException> { createDraftAgreement(buyerConveyancer) }
    }
}