package com.hmlr

import com.hmlr.model.*
import com.hmlr.states.*
import net.corda.core.crypto.Crypto
import net.corda.core.identity.CordaX500Name
import net.corda.finance.POUNDS
import net.corda.testing.core.TestIdentity
import java.security.PrivateKey
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

abstract class AbstractContractsStatesTestUtils {

    val ALICE = TestIdentity(CordaX500Name(organisation = "Alice", locality = "TestLand", country = "US"))
    val BOB = TestIdentity(CordaX500Name(organisation = "Bob", locality = "TestCity", country = "US"))
    var CHARLIE = TestIdentity(CordaX500Name(organisation = "Charlie", locality = "TestVillage", country = "US"))
    var LENDER1 = TestIdentity(CordaX500Name(organisation = "LenderA", locality = "TestVillage", country = "US"))
    var LENDER2 = TestIdentity(CordaX500Name(organisation = "LenderB", locality = "TestVillage", country = "US"))
    var SETTLING_PARTY = TestIdentity(CordaX500Name(organisation = "SellingParty", locality = "TestVillage", country = "US"))
    val titleId = "12345"
    val caseReferenceNumber = "ZQ12345"
    val buyerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val sellerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val wrongBuyerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val wrongSellerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)

    val buyerPublicKey = buyerKeys.public
    val buyerPrivateKey = buyerKeys.private

    val wrongBuyerPrivateKey = wrongBuyerKeys.private

    val sellerPublicKey = sellerKeys.public
    val sellerPrivateKey = sellerKeys.private

    val wrongSellerPublicKey = wrongSellerKeys.public
    val wrongSellerPrivateKey = wrongSellerKeys.private

    val location = Address("10", "Digital Street", "Avon", "Bristol", "England", "BS2 8EN")
    val other = CustomParty("John", "Smith", "125464", location, UserType.INDIVIDUAL, "seller@example.com", "12345678910", true, signature = null, publicKey = wrongSellerPublicKey)
    val seller = CustomParty("Lisa", "White", "1", location, UserType.INDIVIDUAL, "lisa.white@example.com", "07700900354", true, signature = null, publicKey = sellerPublicKey)
    val buyer = CustomParty("David", "Jones", "543545", location, UserType.INDIVIDUAL, "buyer@example.com", "12345678910", true, signature = null, publicKey = buyerPublicKey)
    val prop = LandTitleProperties(location, BOB.party,LENDER1.party ,seller)
    val restrictionText = "No disposition of the registered estate by the proprietor of the registered estate is to be registered"
    val charge = Charge(Instant.now(), LENDER1.party, 100.POUNDS)
    val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.NO_ACTION, false, charge)
    val proposedChargeOrRestrictionState = ProposedChargesAndRestrictionsState(titleID = titleId,  ownerConveyancer = BOB.party, buyerConveyancer = null, charges =  setOf(charge), restrictions = setOf(chargeRestriction), addNewChargeConsented = false, dischargeConsented = false, status = DTCConsentStatus.ISSUED, participants = listOf(ALICE.party, BOB.party, LENDER1.party))
    val landTitleState = LandTitleState(titleID = titleId, landTitleProperties = prop, titleIssuer = ALICE.party, titleType = TitleType.WHOLE, lastSoldValue = null, status = LandTitleStatus.ISSUED, charges =  setOf(charge), restrictions = setOf(chargeRestriction), proposedChargeOrRestrictionLinearId = proposedChargeOrRestrictionState.linearId.toString())
    val requestIssuanceState = RequestIssuanceState(titleId, ALICE.party, BOB.party, seller, RequestIssuanceStatus.PENDING)
    var agreementState = LandAgreementState(titleId, buyer, seller, CHARLIE.party, BOB.party, LocalDate.now(), LocalDate.now().atStartOfDay().plusDays(14).toInstant(ZoneOffset.UTC), 9.0, 1000.POUNDS, 50.POUNDS, null, 950.POUNDS, landTitleState.linearId.toString(), listOf(), TitleGuarantee.FULL, AgreementStatus.CREATED, false, "")
    val paymentConfirmationState = PaymentConfirmationState(buyer = buyer, seller = seller, status = PaymentConfirmationStatus.ISSUED, settlingParty = SETTLING_PARTY.party, purchasePrice = agreementState.purchasePrice, titleID = titleId, landAgreementStateLinearId = agreementState.linearId.toString(), participants = listOf(BOB.party, CHARLIE.party, SETTLING_PARTY.party), buyerConveyancer = CHARLIE.party)
    val landTitleStateWithBuyerConveyancer = landTitleState.copy(participants = landTitleState.participants + agreementState.buyerConveyancer, status = LandTitleStatus.ASSIGN_BUYER_CONVEYANCER)

    protected fun sign(message: String, key: PrivateKey): ByteArray {
        return Crypto.doSign(Crypto.RSA_SHA256, key, message.toByteArray())
    }
}

