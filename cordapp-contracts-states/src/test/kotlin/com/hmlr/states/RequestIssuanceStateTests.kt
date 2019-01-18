package com.hmlr.states

import com.hmlr.ALICE
import com.hmlr.BOB
import com.hmlr.model.Address
import com.hmlr.model.CustomParty
import com.hmlr.model.RequestIssuanceStatus
import com.hmlr.requestIssuanceState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RequestIssuanceStateTests {

    /**
     * Test 1
     */
    @Test
    fun `hasTitleIDFieldOfCorrectType`() {
        // Does the titleID field exist?
        RequestIssuanceState::class.java.getDeclaredField("titleID")
        // Is the titleID field of the correct type?
        assertEquals(RequestIssuanceState::class.java.getDeclaredField("titleID").type, String::class.java)
    }

    /**
    * Test 3
    */
    @Test
    fun `hasTitleIssuerOfCorrectType`() {
        // Does the titleIssuer field exist?
        RequestIssuanceState::class.java.getDeclaredField("titleIssuer")
        // Is the titleIssuer field of the correct type?
        assertEquals(RequestIssuanceState::class.java.getDeclaredField("titleIssuer").type, Party::class.java)
    }

    /**
     * Test 4
     */
    @Test
    fun `hasConveyancerOfCorrectType`() {
        // Does the conveyancer field exist?
        RequestIssuanceState::class.java.getDeclaredField("sellerConveyancer")
        // Is the conveyancer field of the correct type?
        assertEquals(RequestIssuanceState::class.java.getDeclaredField("sellerConveyancer").type, Party::class.java)
    }

    /**
    * Test 5
    */
    @Test
    fun `hasSellerOfCorrectType` (){
        // Does seller field exist?
        val seller = RequestIssuanceState::class.java.getDeclaredField("seller")
        // Is the seller field of correct type?
        assertEquals(seller.type, CustomParty::class.java)
    }

    /**
     * Test 6
     */
    @Test
    fun `isRequestIssuanceStateOfCorrectType` (){
        // Is it a linear state?
        assert(LinearState::class.java.isAssignableFrom(RequestIssuanceState::class.java))
    }

    /**
     * Test 7
     */
    @Test
    fun `isTitleIssuerAParticipant` (){
        assertNotEquals(requestIssuanceState.participants.indexOf(ALICE.party), -1)
    }

    /**
     * Test 8
     */
    @Test
    fun `isConveyancerAParticipant` (){
        assertNotEquals(requestIssuanceState.participants.indexOf(BOB.party), -1)
    }

    /**
     * Test 9
     *
     */
    @Test
    fun hasLinearIdFieldOfCorrectType() {
        // Does the linearId field exist?
        RequestIssuanceState::class.java.getDeclaredField("linearId")
        // Is the linearId field of the correct type?
        assertEquals(RequestIssuanceState::class.java.getDeclaredField("linearId").type, UniqueIdentifier::class.java)
    }

    /**
     * Test 10
     */
    @Test
    fun `hasStatusFieldOfCorrectType`() {
        // Does the referenceNumber field exist?
        RequestIssuanceState::class.java.getDeclaredField("status")
        // Is the referenceNumber field of the correct type?
        assertEquals(RequestIssuanceState::class.java.getDeclaredField("status").type, RequestIssuanceStatus::class.java)
    }
}