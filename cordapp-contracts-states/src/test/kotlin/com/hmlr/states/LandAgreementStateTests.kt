package com.hmlr.states

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.AgreementStatus
import com.hmlr.model.CustomParty
import com.hmlr.model.TitleGuarantee
import net.corda.core.contracts.Amount
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class LandAgreementStateTests : AbstractContractsStatesTestUtils() {

    /**
     * Test 1.
     */
    @Test
    fun `has TitleID Field Of Correct Type`() {
        // Does the titleID field exist?
        LandAgreementState::class.java.getDeclaredField("titleID")
        // Is the titleID field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("titleID").type, String::class.java)
    }

    /**
     * Test 2.
     */
    @Test
    fun `has Buyer Field Of Correct Type`() {
        // Does the buyer field exist?
        LandAgreementState::class.java.getDeclaredField("buyer")
        // Is the buyer field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("buyer").type, CustomParty::class.java)
    }

    /**
     * Test 3.
     */
    @Test
    fun `has Seller Field Of Correct Type`() {
        // Does the seller field exists
        LandAgreementState::class.java.getDeclaredField("seller")
        // Is the seller field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("seller").type, CustomParty::class.java)
    }

    /**
     * Test 4.
     */
    @Test
    fun `has Buyer Conveyancer Field Of Correct Type`() {
        // Does the buyerConveyancer field exists
        LandAgreementState::class.java.getDeclaredField("buyerConveyancer")
        // Is the buyerConveyancer field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("buyerConveyancer").type, Party::class.java)
    }

    /**
     * Test 5.
     */
    @Test
    fun `has Seller Conveyancer Field Of Correct Type`() {
        // Does the sellerConveyancer field exists
        LandAgreementState::class.java.getDeclaredField("sellerConveyancer")
        // Is the sellerConveyancer field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("sellerConveyancer").type, Party::class.java)
    }

    /**
     * Test 6.
     */
    @Test
    fun `has CreationDate Field Of Correct Type`() {
        // Does the creationDate field exists
        LandAgreementState::class.java.getDeclaredField("creationDate")
        // Is the creationDate field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("creationDate").type, LocalDate::class.java)
    }

    /**
     * Test 7.
     */
    @Test
    fun `has CompletionDate Field Of Correct Type`() {
        // Does the completionDate field exists
        LandAgreementState::class.java.getDeclaredField("completionDate")
        // Is the completionDate field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("completionDate").type, Instant::class.java)
    }

    /**
     * Test 8.
     */
    @Test
    fun `has ContractRate Field Of Correct Type`() {
        // Does the contractRate field exists
        LandAgreementState::class.java.getDeclaredField("contractRate")
        // Is the contractRate field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("contractRate").type, Double::class.java)
    }

    /**
     * Test 9.
     */
    @Test
    fun `has PurchasePrice Field Of Correct Type`() {
        // Does the purchasePrice field exists
        LandAgreementState::class.java.getDeclaredField("purchasePrice")
        // Is the purchasePrice field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("purchasePrice").type, Amount::class.java)
    }

    /**
     * Test 10.
     */
    @Test
    fun `has Deposit Field Of Correct Type`() {
        // Does the deposit field exists
        LandAgreementState::class.java.getDeclaredField("deposit")
        // Is the deposit field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("deposit").type, Amount::class.java)
    }

    /**
     * Test 11.
     */
    @Test
    fun `has ContentsPrice Field Of Correct Type`() {
        // Does the contentsPrice field exists
        LandAgreementState::class.java.getDeclaredField("contentsPrice")
        // Is the contentsPrice field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("contentsPrice").type, Amount::class.java)
    }

    /**
     * Test 12.
     */
    @Test
    fun `has Balance Field Of Correct Type`() {
        // Does the balance field exists
        LandAgreementState::class.java.getDeclaredField("balance")
        // Is the balance field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("balance").type, Amount::class.java)
    }

    /**
     * Test 13.
     */
    @Test
    fun `has TitleStateLinearId Field Of Correct Type`() {
        // Does the titleStateLinearId field exists
        LandAgreementState::class.java.getDeclaredField("titleStateLinearId")
        // Is the titleStateLinearId field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("titleStateLinearId").type, String::class.java)
    }

    /**
     * Test 14.
     */
    @Test
    fun `has SpecificIncumbrances Field Of Correct Type`() {
        // Does the specificIncumbrances field exists
        LandAgreementState::class.java.getDeclaredField("specificIncumbrances")
        // Is the specificIncumbrances field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("specificIncumbrances").type, List::class.java)
    }

    /**
     * Test 15.
     */
    @Test
    fun `has AgreementStatus Field Of Correct Type`() {
        // Does the status field exists
        LandAgreementState::class.java.getDeclaredField("status")
        // Is the status field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("status").type, AgreementStatus::class.java)
    }

    /**
     * Test 16.
     */
    @Test
    fun `has LinearId Field Of Correct Type`() {
        // Does the linearId field exist?
        LandAgreementState::class.java.getDeclaredField("linearId")
        // Is the linearId field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("linearId").type, UniqueIdentifier::class.java)
    }

    /**
     * Test 17.
     */
    @Test
    fun `is LinearState`() {
        assert(LandAgreementState::class.java.isAssignableFrom(agreementState::class.java))
    }

    /**
     * Test 18.
     *
     */
    @Test
    fun `buyer Conveyancer Is Participant`() {
        assertNotEquals(agreementState.participants.indexOf(CHARLIE.party), -1)
    }

    /**
     * Test 19.
     *
     */
    @Test
    fun `seller Conveyancer Is Participant`() {
        assertNotEquals(agreementState.participants.indexOf(BOB.party), -1)
    }

    /**
     * Test 20.
     *
     */
    @Test
    fun `has TitleGuarantee Field Of Correct Type`() {
        // Does the TitleGuarantee field exists
        LandAgreementState::class.java.getDeclaredField("titleGuarantee")
        // Is the TitleGuarantee field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("titleGuarantee").type, TitleGuarantee::class.java)

    }

    /**
     * Test 21.
     *
     */
    @Test
    fun `has isMortgageTermsAdded Field Of Correct Type`() {
        // Does the status field exists
        LandAgreementState::class.java.getDeclaredField("isMortgageTermsAdded")
        // Is the status field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("isMortgageTermsAdded").type, Boolean::class.java)
    }


    /**
     * Test 22.
     */
    @Test
    fun `has paymentConfirmationStateLinearId Field Of Correct Type`() {
        // Does the paymentConfirmationStateLinearId field exist?
        LandAgreementState::class.java.getDeclaredField("paymentConfirmationStateLinearId")
        // Is the paymentConfirmationStateLinearId field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("paymentConfirmationStateLinearId").type, String::class.java)
    }
}