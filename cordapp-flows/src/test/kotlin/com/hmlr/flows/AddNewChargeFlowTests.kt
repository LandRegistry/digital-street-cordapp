package com.hmlr.flows

import com.hmlr.model.DTCConsentStatus
import com.hmlr.states.ProposedChargesAndRestrictionsState
import net.corda.core.transactions.SignedTransaction
import org.junit.Test
import kotlin.test.assertFailsWith

class AddNewChargeFlowTests : AbstractFlowTestUtils() {

    @Test
    fun `Add new charge for land title`(){
        addNewCharge(buyerConveyancer)
        mockNetwork.waitQuiescent()

        // check for Charge and Restriction state on seller conveyancer node
        sellerConveyancer.transaction {
            val chargeAndRestrictionState = sellerConveyancer.services.vaultService.queryBy(ProposedChargesAndRestrictionsState::class.java).states
            assert(chargeAndRestrictionState.size == 1)
            assert(chargeAndRestrictionState[0].state.data.status == DTCConsentStatus.CONSENT_FOR_NEW_CHARGE)
        }

        // check for Charge and Restriction state on seller lender node
        sellerLender.transaction {
            val chargeAndRestrictionState = sellerLender.services.vaultService.queryBy(ProposedChargesAndRestrictionsState::class.java).states
            assert(chargeAndRestrictionState.size == 1)
            assert(chargeAndRestrictionState[0].state.data.status == DTCConsentStatus.CONSENT_FOR_NEW_CHARGE)
        }

        // check for Charge and Restriction state on buyer conveyancer node
        buyerConveyancer.transaction {
            val chargeAndRestrictionState = buyerConveyancer.services.vaultService.queryBy(ProposedChargesAndRestrictionsState::class.java).states
            assert(chargeAndRestrictionState.size == 1)
            assert(chargeAndRestrictionState[0].state.data.status == DTCConsentStatus.CONSENT_FOR_NEW_CHARGE)
        }
    }

    @Test
    fun `Add new charge should only be created by the buyer's conveyancer`() {
        // Add new charge with wrong conveyancer
        assertFailsWith<SignedTransaction.SignaturesMissingException> { addNewCharge(sellerConveyancer) }
    }
}