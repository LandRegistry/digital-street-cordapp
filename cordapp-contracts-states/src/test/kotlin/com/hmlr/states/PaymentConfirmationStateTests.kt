package com.hmlr.states

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.CustomParty
import com.hmlr.model.PaymentConfirmationStatus
import net.corda.core.contracts.Amount
import net.corda.core.identity.Party
import org.junit.Test
import kotlin.test.assertEquals

class PaymentConfirmationStateTests: AbstractContractsStatesTestUtils() {


    /**
     * Test 1.
     */
    @Test
    fun `has TitleID Field Of Correct Type`() {
        // Does the titleID field exist?
        PaymentConfirmationState::class.java.getDeclaredField("titleID")
        // Is the titleID field of the correct type?
        assertEquals(PaymentConfirmationState::class.java.getDeclaredField("titleID").type, String::class.java)
    }

    /**
     * Test 2.
     *
     */
    @Test
    fun `has buyer Field Of Correct Type`() {
        // Does the buyer field exist?
         PaymentConfirmationState::class.java.getDeclaredField("buyer").type
        // Is the buyer field of the correct type?
        assertEquals(PaymentConfirmationState::class.java.getDeclaredField("buyer").type, CustomParty::class.java)
    }

    /**
     * Test 3.
     *
     */
    @Test
    fun `has seller Field Of Correct Type`() {
        // Does the seller field exist?
        PaymentConfirmationState::class.java.getDeclaredField("seller").type
        // Is the seller field of the correct type?
        assertEquals(PaymentConfirmationState::class.java.getDeclaredField("seller").type, CustomParty::class.java)
    }

    /**
     * Test 4.
     *
     */
    @Test
    fun `has settlingParty Field Of Correct Type`() {
        // Does the settlingParty field exist?
        PaymentConfirmationState::class.java.getDeclaredField("settlingParty")
        // Is the settlingParty field of the correct type?
        assertEquals(PaymentConfirmationState::class.java.getDeclaredField("settlingParty").type, Party::class.java)
    }

    /**
     * Test 5.
     */
    @Test
    fun `has PurchasePrice Field Of Correct Type`() {
        // Does the purchasePrice field exists
        PaymentConfirmationState::class.java.getDeclaredField("purchasePrice")
        // Is the purchasePrice field of the correct type?
        assertEquals(PaymentConfirmationState::class.java.getDeclaredField("purchasePrice").type, Amount::class.java)
    }

    /**
     * Test 6.
     */
    @Test
    fun `has agreementStateLinearId Field Of Correct Type`() {
        // Does the agreementStateLinearId field exists
        PaymentConfirmationState::class.java.getDeclaredField("landAgreementStateLinearId")
        // Is the agreementStateLinearId field of the correct type?
        assertEquals(PaymentConfirmationState::class.java.getDeclaredField("landAgreementStateLinearId").type, String::class.java)
    }

    /**
     * Test 7.
     */
    @Test
    fun `has status Field Of Correct Type`() {
        // Does the status field exists
        PaymentConfirmationState::class.java.getDeclaredField("status")
        // Is the status field of the correct type?
        assertEquals(PaymentConfirmationState::class.java.getDeclaredField("status").type, PaymentConfirmationStatus::class.java)
    }

    /**
     * Test 8.
     *
     */
    @Test
    fun `has buyerConveyancer Field Of Correct Type`() {
        // Does the buyerConveyancer field exist?
        PaymentConfirmationState::class.java.getDeclaredField("buyerConveyancer")
        // Is the buyerConveyancer field of the correct type?
        assertEquals(PaymentConfirmationState::class.java.getDeclaredField("buyerConveyancer").type, Party::class.java)
    }
}