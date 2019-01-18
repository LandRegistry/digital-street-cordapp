package com.hmlr.states

import com.hmlr.ALICE
import com.hmlr.BOB
import com.hmlr.landTitleState
import com.hmlr.model.CustomParty
import com.hmlr.model.Address
import com.hmlr.model.LandTitleStatus
import net.corda.core.contracts.Amount
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class LandTitleStateTests {

    /**
     * Test 1.
     *
     */
    @Test
    fun `hasTitleIDFieldOfCorrectType`() {
        // Does the titleID field exist?
        LandTitleState::class.java.getDeclaredField("titleID")
        // Is the titleID field of the correct type?
        assertEquals(LandTitleState::class.java.getDeclaredField("titleID").type, String::class.java)
    }

    /**
     * Test 2.
     *
     * */
    @Test
    fun hasLocationFieldOfCorrectType(){
        // Does the location field exist?
        var propClass =  LandTitleState::class.java.getDeclaredField("landTitleProperties").type
        // Is the location field of the correct type?
        assertEquals(propClass.getDeclaredField("address").type, Address::class.java)
    }

    /**
     * Test 3.
     *
     */
    @Test
    fun hasDelegatedFieldOfCorrectType() {
        // Does the delegatedParty field exist?
        var propClass =  LandTitleState::class.java.getDeclaredField("landTitleProperties").type
        // Is the delegatedParty field of the correct type?
        assertEquals(propClass.getDeclaredField("ownerConveyancer").type, Party::class.java)
    }

    /**
     * Test 4.
     *
     */
    @Test
    fun hasOwnerFieldOfCorrectType() {
        // Does the location field exist?
        var propClass =  LandTitleState::class.java.getDeclaredField("landTitleProperties").type
        // Is the location field of the correct type?
        assertEquals(propClass.getDeclaredField("owner").type, CustomParty::class.java)
    }

    /**
     * Test 5.
     *
     */
    @Test
    fun hasIssuerFieldOfCorrectType() {
        // Does the lender field exist?
        LandTitleState::class.java.getDeclaredField("titleIssuer")
        // Is the issuer field of the correct type?
        assertEquals(LandTitleState::class.java.getDeclaredField("titleIssuer").type, Party::class.java)
    }


    /**
     * Test 6.
     *
     */
    @Test
    fun issuerIsParticipant() {
        assertNotEquals(landTitleState.participants.indexOf(ALICE.party), -1)
    }

    /**
     * Test 7.
     *
     */
    @Test
    fun delegatedPartyIsParticipant() {
        assertNotEquals(landTitleState.participants.indexOf(BOB.party), -1)
    }

    /**
     * Test 8.
     *
     */
    @Test
    fun isLinearState() {
        assert(LinearState::class.java.isAssignableFrom(landTitleState::class.java))
    }

    /**
     * Test 9.
     *
     */
    @Test
    fun hasLinearIdFieldOfCorrectType() {
        // Does the linearId field exist?
        LandTitleState::class.java.getDeclaredField("linearId")
        // Is the linearId field of the correct type?
        assertEquals(LandTitleState::class.java.getDeclaredField("linearId").type, UniqueIdentifier::class.java)
    }


    /**
     * Test 10.
     *
     */
    @Test
    fun hasOfferPriceFieldOfCorrectType() {
        // Does the location field exist?
        var propClass =  LandTitleState::class.java.getDeclaredField("lastSoldValue").type
        // Is the location field of the correct type?
        assertEquals(propClass, Amount::class.java)
    }

    /**
     * Test 11.
     *
     */
    @Test
    fun hasStatusFieldOfCorrectType() {
        // Does the location field exist?
        var propClass =  LandTitleState::class.java.getDeclaredField("status").type
        // Is the location field of the correct type?
        assertEquals(propClass, LandTitleStatus::class.java)
    }
}
