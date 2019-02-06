package com.hmlr.flows

import com.hmlr.model.LandTitleStatus
import com.hmlr.model.PaymentConfirmationStatus
import com.hmlr.states.LandTitleState
import com.hmlr.states.PaymentConfirmationState
import org.junit.Test

class LandTitleTransferTests : AbstractFlowTestUtils() {

    @Test
    fun `transfer land title successfully`(){
        // call buyer sign flow
        buyerSignAgreement(buyerConveyancer, buyerPrivateKey)
        mockNetwork.waitQuiescent()


        // check for land title states
        issuer.transaction {
            val states = issuer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  LandTitleStatus.TRANSFERRED)
            assert(states[0].state.data.landTitleProperties.owner.userID == buyer.userID)
            assert(states[0].state.data.landTitleProperties.ownerConveyancer == buyerConveyancer.info.legalIdentities.single())
        }

        buyerConveyancer.transaction {
            val states = buyerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  LandTitleStatus.TRANSFERRED)
            assert(states[0].state.data.landTitleProperties.owner.userID == buyer.userID)
            assert(states[0].state.data.landTitleProperties.ownerConveyancer == buyerConveyancer.info.legalIdentities.single())
        }

        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  LandTitleStatus.TRANSFERRED)
            assert(states[0].state.data.landTitleProperties.owner.userID == buyer.userID)
            assert(states[0].state.data.landTitleProperties.ownerConveyancer == buyerConveyancer.info.legalIdentities.single())
        }

        sellerLender.transaction {
            val states = sellerLender.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  LandTitleStatus.TRANSFERRED)
            assert(states[0].state.data.landTitleProperties.owner.userID == buyer.userID)
            assert(states[0].state.data.landTitleProperties.ownerConveyancer == buyerConveyancer.info.legalIdentities.single())
        }

        settlingParty.transaction {
            val states = settlingParty.services.vaultService.queryBy(PaymentConfirmationState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  PaymentConfirmationStatus.CONFIRM_FUNDS_RELEASED)
        }
    }
}