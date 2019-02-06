package com.hmlr.flows

import com.hmlr.model.LandTitleStatus
import com.hmlr.states.LandTitleState
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
            assert(states[0].state.data.landTitleProperties.ownerLender == buyerLender.info.legalIdentities.single())
        }

        buyerConveyancer.transaction {
            val states = buyerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  LandTitleStatus.TRANSFERRED)
            assert(states[0].state.data.landTitleProperties.owner.userID == buyer.userID)
            assert(states[0].state.data.landTitleProperties.ownerConveyancer == buyerConveyancer.info.legalIdentities.single())
            assert(states[0].state.data.landTitleProperties.ownerLender == buyerLender.info.legalIdentities.single())
        }

        buyerLender.transaction {
            val states = sellerLender.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  LandTitleStatus.TRANSFERRED)
            assert(states[0].state.data.landTitleProperties.owner.userID == buyer.userID)
            assert(states[0].state.data.landTitleProperties.ownerConveyancer == buyerConveyancer.info.legalIdentities.single())
            assert(states[0].state.data.landTitleProperties.ownerLender == buyerLender.info.legalIdentities.single())
        }
    }
}