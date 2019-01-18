package com.hmlr.states

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.DTCConsentStatus
import net.corda.core.identity.Party
import org.junit.Test
import kotlin.test.assertEquals

class ProposedChargesAndRestrictionsTests: AbstractContractsStatesTestUtils() {

    /**
     * Test 1.
     *
     */
    @Test
    fun `has TitleID Field Of Correct Type`() {
        // Does the titleID field exist?
        ProposedChargesAndRestrictionsState::class.java.getDeclaredField("titleID")
        // Is the titleID field of the correct type?
        assertEquals(ProposedChargesAndRestrictionsState::class.java.getDeclaredField("titleID").type, String::class.java)
    }

    /**
     * Test 2.
     *
     */
    @Test
    fun `has Restriction Field Of Correct Type`() {
        // Does the restrictions field exist?
        val propClass =  ProposedChargesAndRestrictionsState::class.java.getDeclaredField("restrictions").type
        // Is the restrictions field of the correct type?
        assertEquals(propClass, Set::class.java)
    }

    /**
     * Test 3.
     *
     */
    @Test
    fun `has Charge Field Of Correct Type`() {
        // Does the charges field exist?
        val propClass =  ProposedChargesAndRestrictionsState::class.java.getDeclaredField("charges").type
        // Is the charges field of the correct type?
        assertEquals(propClass, Set::class.java)
    }

    /**
     * Test 4.
     *
     */
    @Test
    fun `has dischargeConsented Field Of Correct Type`() {
        // Does the dischargeConsented field exist?
        ProposedChargesAndRestrictionsState::class.java.getDeclaredField("dischargeConsented")
        // Is the dischargeConsented field of the correct type?
        assertEquals(ProposedChargesAndRestrictionsState::class.java.getDeclaredField("dischargeConsented").type, Boolean::class.java)
    }

    /**
     * Test 5.
     *
     */
    @Test
    fun `has addNewChargeConsented Field Of Correct Type`() {
        // Does the addNewChargeConsented field exist?
        ProposedChargesAndRestrictionsState::class.java.getDeclaredField("addNewChargeConsented")
        // Is the addNewChargeConsented field of the correct type?
        assertEquals(ProposedChargesAndRestrictionsState::class.java.getDeclaredField("addNewChargeConsented").type, Boolean::class.java)
    }

    /**
     * Test 6.
     *
     */
    @Test
    fun `has status Field Of Correct Type`() {
        // Does the status field exist?
        ProposedChargesAndRestrictionsState::class.java.getDeclaredField("status")
        // Is the status field of the correct type?
        assertEquals(ProposedChargesAndRestrictionsState::class.java.getDeclaredField("status").type, DTCConsentStatus::class.java)
    }

    /**
     * Test 7.
     *
     */
    @Test
    fun `has ownerConveyancer Field Of Correct Type`() {
        // Does the ownerConveyancer field exist?
        ProposedChargesAndRestrictionsState::class.java.getDeclaredField("ownerConveyancer")
        // Is the ownerConveyancer field of the correct type?
        assertEquals(ProposedChargesAndRestrictionsState::class.java.getDeclaredField("ownerConveyancer").type, Party::class.java)
    }

    /**
     * Test 8.
     *
     */
    @Test
    fun `has buyerConveyancer Field Of Correct Type`() {
        // Does the buyerConveyancer field exist?
        ProposedChargesAndRestrictionsState::class.java.getDeclaredField("buyerConveyancer")
        // Is the buyerConveyancer field of the correct type?
        assertEquals(ProposedChargesAndRestrictionsState::class.java.getDeclaredField("buyerConveyancer").type, Party::class.java)
    }
}