package com.hmlr.states

import com.hmlr.model.CustomParty
import com.hmlr.AbstractContractsStatesTestUtils
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class InstructConveyancerTests: AbstractContractsStatesTestUtils() {

    /**
     * Test 1.
     */
    @Test
    fun `has Title ID Field Of Correct Type`() {
        // Does the titleID field exist?
        InstructConveyancerState::class.java.getDeclaredField("titleID")
        // Is the titleID field of the correct type?
        assertEquals(InstructConveyancerState::class.java.getDeclaredField("titleID").type, String::class.java)
    }

    /**
     * Test 2.
     */
    @Test
    fun `has Buyer Field Of Correct Type`() {
        // Does the user field exist?
        InstructConveyancerState::class.java.getDeclaredField("user")
        // Is the user field of the correct type?
        assertEquals(InstructConveyancerState::class.java.getDeclaredField("user").type, CustomParty::class.java)
    }

    /**
     * Test 3.
     *
     */
    @Test
    fun `has Issuer Field Of Correct Type`() {
        // Does the lender field exist?
        InstructConveyancerState::class.java.getDeclaredField("titleIssuer")
        // Is the issuer field of the correct type?
        assertEquals(InstructConveyancerState::class.java.getDeclaredField("titleIssuer").type, Party::class.java)
    }

    /**
     * Test 4.
     */
    @Test
    fun `has Conveyancer Field Of Correct Type`() {
        // Does the sellerConveyancer field exists
        InstructConveyancerState::class.java.getDeclaredField("conveyancer")
        // Is the sellerConveyancer field of the correct type?
        assertEquals(InstructConveyancerState::class.java.getDeclaredField("conveyancer").type, Party::class.java)
    }

    /**
     * Test 5.
     */
    @Test
    fun `has Case Reference Number Field Of Correct Type`() {
        // Does the titleID field exist?
        InstructConveyancerState::class.java.getDeclaredField("caseReferenceNumber")
        // Is the titleID field of the correct type?
        assertEquals(InstructConveyancerState::class.java.getDeclaredField("caseReferenceNumber").type, String::class.java)
    }

    /**
     * Test 6.
     */
    @Test
    fun `has LinearId Field Of Correct Type`() {
        // Does the linearId field exist?
        InstructConveyancerState::class.java.getDeclaredField("linearId")
        // Is the linearId field of the correct type?
        assertEquals(InstructConveyancerState::class.java.getDeclaredField("linearId").type, UniqueIdentifier::class.java)
    }

    /**
     * Test 7.
     */
    @Test
    fun `is LinearState`() {
        assert(InstructConveyancerState::class.java.isAssignableFrom(instructConveyancerState::class.java))
    }

    /**
     * Test 8.
     */
    @Test
    fun `titleIssuer Is Participant`() {
        assertNotEquals(instructConveyancerState.participants.indexOf(ALICE.party), -1)
    }

    /**
     * Test 9.
     */
    @Test
    fun `conveyancer Is Participant`() {
        assertNotEquals(instructConveyancerState.participants.indexOf(BOB.party), -1)
    }
}