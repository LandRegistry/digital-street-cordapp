package com.hmlr.states

import com.hmlr.*
import com.hmlr.model.AgreementStatus
import com.hmlr.model.CustomParty
import com.hmlr.model.TitleGuarantee
import net.corda.core.contracts.Amount
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class LandAgreementStateTests {

    /**
     * Test 1.
     */
    @Test
    fun hasTitleIDFieldOfCorrectType() {
        // Does the titleID field exist?
        LandAgreementState::class.java.getDeclaredField("titleID")
        // Is the titleID field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("titleID").type, String::class.java)
    }

    /**
     * Test 2.
     */
    @Test
    fun hasBuyerFieldOfCorrectType() {
        // Does the buyer field exist?
        LandAgreementState::class.java.getDeclaredField("buyer")
        // Is the buyer field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("buyer").type, CustomParty::class.java)
    }

    /**
     * Test 3.
     */
    @Test
    fun hasSellerFieldOfCorrectType() {
        // Does the seller field exists
        LandAgreementState::class.java.getDeclaredField("seller")
        // Is the seller field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("seller").type, CustomParty::class.java)
    }

    /**
     * Test 4.
     */
    @Test
    fun hasBuyerConveyancerFieldOfCorrectType() {
        // Does the buyerConveyancer field exists
        LandAgreementState::class.java.getDeclaredField("buyerConveyancer")
        // Is the buyerConveyancer field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("buyerConveyancer").type, Party::class.java)
    }

    /**
     * Test 5.
     */
    @Test
    fun hasSellerConveyancerFieldOfCorrectType() {
        // Does the sellerConveyancer field exists
        LandAgreementState::class.java.getDeclaredField("sellerConveyancer")
        // Is the sellerConveyancer field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("sellerConveyancer").type, Party::class.java)
    }

    /**
     * Test 6.
     */
    @Test
    fun hasCreationDateFieldOfCorrectType() {
        // Does the creationDate field exists
        LandAgreementState::class.java.getDeclaredField("creationDate")
        // Is the creationDate field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("creationDate").type, LocalDate::class.java)
    }

    /**
     * Test 7.
     */
    @Test
    fun hasCompletionDateFieldOfCorrectType() {
        // Does the completionDate field exists
        LandAgreementState::class.java.getDeclaredField("completionDate")
        // Is the completionDate field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("completionDate").type, LocalDate::class.java)
    }

    /**
     * Test 8.
     */
    @Test
    fun hasContractRateFieldOfCorrectType() {
        // Does the contractRate field exists
        LandAgreementState::class.java.getDeclaredField("contractRate")
        // Is the contractRate field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("contractRate").type, Double::class.java)
    }

    /**
     * Test 9.
     */
    @Test
    fun hasPurchasePriceFieldOfCorrectType() {
        // Does the purchasePrice field exists
        LandAgreementState::class.java.getDeclaredField("purchasePrice")
        // Is the purchasePrice field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("purchasePrice").type, Amount::class.java)
    }

    /**
     * Test 10.
     */
    @Test
    fun hasDepositFieldOfCorrectType() {
        // Does the deposit field exists
        LandAgreementState::class.java.getDeclaredField("deposit")
        // Is the deposit field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("deposit").type, Amount::class.java)
    }

    /**
     * Test 11.
     */
    @Test
    fun hasContentsPriceFieldOfCorrectType() {
        // Does the contentsPrice field exists
        LandAgreementState::class.java.getDeclaredField("contentsPrice")
        // Is the contentsPrice field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("contentsPrice").type, Amount::class.java)
    }

    /**
     * Test 12.
     */
    @Test
    fun hasBalanceFieldOfCorrectType() {
        // Does the balance field exists
        LandAgreementState::class.java.getDeclaredField("balance")
        // Is the balance field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("balance").type, Amount::class.java)
    }

    /**
     * Test 13.
     */
    @Test
    fun hasTitleStateLinearIdFieldOfCorrectType() {
        // Does the titleStateLinearId field exists
        LandAgreementState::class.java.getDeclaredField("titleStateLinearId")
        // Is the titleStateLinearId field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("titleStateLinearId").type, String::class.java)
    }

    /**
     * Test 14.
     */
    @Test
    fun hasSpecificIncumbrancesFieldOfCorrectType() {
        // Does the specificIncumbrances field exists
        LandAgreementState::class.java.getDeclaredField("specificIncumbrances")
        // Is the specificIncumbrances field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("specificIncumbrances").type, List::class.java)
    }

    /**
     * Test 15.
     */
    @Test
    fun hasAgreementStatusFieldOfCorrectType() {
        // Does the status field exists
        LandAgreementState::class.java.getDeclaredField("status")
        // Is the status field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("status").type, AgreementStatus::class.java)
    }

    /**
     * Test 16.
     */
    @Test
    fun hasLinearIdFieldOfCorrectType() {
        // Does the linearId field exist?
        LandAgreementState::class.java.getDeclaredField("linearId")
        // Is the linearId field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("linearId").type, UniqueIdentifier::class.java)
    }

    /**
     * Test 17.
     */
    @Test
    fun isLinearState() {
        assert(LandAgreementState::class.java.isAssignableFrom(agreementState::class.java))
    }

    /**
     * Test 18.
     *
     */
    @Test
    fun buyerConveyancerIsParticipant() {
        assertNotEquals(agreementState.participants.indexOf(CHARLIE.party), -1)
    }

    /**
     * Test 19.
     *
     */
    @Test
    fun sellerConveyancerIsParticipant() {
        assertNotEquals(agreementState.participants.indexOf(BOB.party), -1)
    }

    /**
     * Test 20.
     *
     */
    @Test
    fun hasTitleGuaranteeFieldOfCorrectType() {
        // Does the TitleGuarantee field exists
        LandAgreementState::class.java.getDeclaredField("titleGuarantee")
        // Is the TitleGuarantee field of the correct type?
        assertEquals(LandAgreementState::class.java.getDeclaredField("titleGuarantee").type, TitleGuarantee::class.java)

    }
}