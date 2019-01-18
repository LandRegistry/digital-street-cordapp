package com.hmlr.flows

import com.hmlr.common.exception.StateNotFoundOnVaultException
import com.hmlr.model.DTCConsentStatus
import com.hmlr.states.ProposedChargesAndRestrictionsState
import org.junit.Test
import kotlin.test.assertFailsWith

class ConsentForDischargeFlowTests : AbstractFlowTestUtils() {

    @Test
    fun `Consent for discharge on land title`(){
        consentForDischarge(sellerLender)
        mockNetwork.waitQuiescent()

        // check for Charge and Restriction state on seller conveyancer node
        sellerConveyancer.transaction {
            val chargeAndRestrictionState = sellerConveyancer.services.vaultService.queryBy(ProposedChargesAndRestrictionsState::class.java).states
            assert(chargeAndRestrictionState.size == 1)
            assert(chargeAndRestrictionState[0].state.data.status == DTCConsentStatus.CONSENT_FOR_DISCHARGE)
        }

        // check for Charge and Restriction state on seller lender node
        sellerLender.transaction {
            val chargeAndRestrictionState = sellerLender.services.vaultService.queryBy(ProposedChargesAndRestrictionsState::class.java).states
            assert(chargeAndRestrictionState.size == 1)
            assert(chargeAndRestrictionState[0].state.data.status == DTCConsentStatus.CONSENT_FOR_DISCHARGE)
        }
    }

    @Test
    fun `Consent for discharge should only be created by the seller's lender`() {
        // Consent for discharge with wrong user
        assertFailsWith<StateNotFoundOnVaultException> { consentForDischarge(buyerConveyancer) }
    }
}