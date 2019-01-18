package com.hmlr.flows

import com.hmlr.common.exception.StateNotFoundOnVaultException
import com.hmlr.model.DTCConsentStatus
import com.hmlr.states.ProposedChargesAndRestrictionsState
import org.junit.Test
import kotlin.test.assertFailsWith

class RequestForDischargeFlowTests : AbstractFlowTestUtils() {

    @Test
    fun `Request for discharge on land title`(){
        requestForDischarge(sellerConveyancer)
        mockNetwork.waitQuiescent()

        // check for Charge and Restriction state on seller conveyancer node
        sellerConveyancer.transaction {
            val chargeAndRestrictionState = sellerConveyancer.services.vaultService.queryBy(ProposedChargesAndRestrictionsState::class.java).states
            assert(chargeAndRestrictionState.size == 1)
            assert(chargeAndRestrictionState[0].state.data.status == DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
        }

        // check for Charge and Restriction state on seller lender node
        sellerLender.transaction {
            val chargeAndRestrictionState = sellerLender.services.vaultService.queryBy(ProposedChargesAndRestrictionsState::class.java).states
            assert(chargeAndRestrictionState.size == 1)
            assert(chargeAndRestrictionState[0].state.data.status == DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
        }
    }

    @Test
    fun `Request for discharge should only be created by the seller's conveyancer`() {
        // Request for discharge with wrong conveyancer
        assertFailsWith<StateNotFoundOnVaultException> { consentForDischarge(buyerConveyancer) }
    }
}