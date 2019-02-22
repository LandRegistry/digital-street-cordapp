package com.hmlr.states

import com.hmlr.model.CustomParty
import com.hmlr.model.Address
import com.hmlr.model.LandTitleStatus
import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.Restriction
import net.corda.core.contracts.Amount
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class LandTitleStateTests : AbstractContractsStatesTestUtils() {

    /**
     * Test 1.
     *
     */
    @Test
    fun `has TitleID Field Of Correct Type`() {
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
    fun `has Location Field Of Correct Type`() {
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
    fun `has Delegated Field Of Correct Type`() {
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
    fun `has Owner Field Of Correct Type`() {
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
    fun `has Issuer Field Of Correct Type`() {
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
    fun `issuer Is Participant`() {
        assertNotEquals(landTitleState.participants.indexOf(ALICE.party), -1)
    }

    /**
     * Test 7.
     *
     */
    @Test
    fun `delegatedParty Is Participant`() {
        assertNotEquals(landTitleState.participants.indexOf(BOB.party), -1)
    }

    /**
     * Test 8.
     *
     */
    @Test
    fun `is LinearState`() {
        assert(LinearState::class.java.isAssignableFrom(landTitleState::class.java))
    }

    /**
     * Test 9.
     *
     */
    @Test
    fun `has LinearId Field Of Correct Type`() {
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
    fun `has OfferPrice Field Of Correct Type`() {
        // Does the lastSoldValue field exist?
        val propClass =  LandTitleState::class.java.getDeclaredField("lastSoldValue").type
        // Is the lastSoldValue field of the correct type?
        assertEquals(propClass, Amount::class.java)
    }

    /**
     * Test 11.
     *
     */
    @Test
    fun `has Status Field Of Correct Type`() {
        // Does the status field exist?
        val propClass =  LandTitleState::class.java.getDeclaredField("status").type
        // Is the status field of the correct type?
        assertEquals(propClass, LandTitleStatus::class.java)
    }


    /**
     * Test 12.
     *
     */
    @Test
    fun `has Charge Field Of Correct Type`() {
        // Does the charges field exist?
        val propClass =  LandTitleState::class.java.getDeclaredField("charges").type
        // Is the charges field of the correct type?
        assertEquals(propClass, Set::class.java)
    }

    /**
     * Test 13.
     *
     */
    @Test
    fun `has Restriction Field Of Correct Type`() {
        // Does the restrictions field exist?
        val propClass =  LandTitleState::class.java.getDeclaredField("restrictions").type
        // Is the restrictions field of the correct type?
        assertEquals(propClass, Set::class.java)
    }

    /**
     * Test 14.
     *
     */
    @Test
    fun `has proposedChargeOrRestrictionLinearId Field Of Correct Type`() {
        // Does the proposedChargeOrRestrictionLinearId field exist?
        val propClass =  LandTitleState::class.java.getDeclaredField("proposedChargeOrRestrictionLinearId").type
        // Is the proposedChargeOrRestrictionLinearId field of the correct type?
        assertEquals(propClass, String::class.java)
    }

    /**
     * Test 15.
     *
     */
    @Test
    fun `has revenueAndCustom Field Of Correct Type`() {
        // Does the revenueAndCustom field exist?
        LandTitleState::class.java.getDeclaredField("revenueAndCustom")
        // Is the revenueAndCustom field of the correct type?
        assertEquals(LandTitleState::class.java.getDeclaredField("revenueAndCustom").type, Party::class.java)
    }
}
