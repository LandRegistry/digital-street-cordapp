package com.hmlr

import com.hmlr.model.*
import com.hmlr.states.LandAgreementState
import com.hmlr.states.LandTitleState
import com.hmlr.states.RequestIssuanceState
import net.corda.core.crypto.Crypto
import net.corda.core.identity.CordaX500Name
import net.corda.finance.POUNDS
import net.corda.testing.core.TestIdentity
import java.time.LocalDate

val ALICE = TestIdentity(CordaX500Name(organisation = "Alice", locality = "TestLand", country = "US"))
val BOB = TestIdentity(CordaX500Name(organisation = "Bob", locality = "TestCity", country = "US"))
var CHARLIE = TestIdentity(CordaX500Name(organisation = "Charlie", locality = "TestVillage", country = "US"))
val titleId = "12345"
val buyerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
val sellerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
val wrongBuyerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
val wrongSellerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)

val buyerPublicKey = buyerKeys.public
val buyerPrivateKey = buyerKeys.private

val wrongBuyerPublicKey = wrongBuyerKeys.public
val wrongBuyerPrivateKey = wrongBuyerKeys.private

val sellerPublicKey = sellerKeys.public
val sellerPrivateKey = sellerKeys.private

val wrongSellerPublicKey = wrongSellerKeys.public
val wrongSellerPrivateKey = wrongSellerKeys.private

val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
val other = CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "seller@example.com", "12345678910", true, signature = null, publicKey = wrongSellerPublicKey)
val seller = CustomParty("Lisa", "White", "125464", location, UserType.INDIVIDUAL, "seller@example.com", "12345678910", true, signature = null, publicKey = sellerPublicKey)
val buyer = CustomParty("David", "Jones", "543545", location, UserType.INDIVIDUAL, "buyer@example.com", "12345678910", true, signature = null, publicKey = buyerPublicKey)
val prop = LandTitleProperties(location, BOB.party, seller)
val landTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party,  titleType = TitleType.WHOLE, lastSoldValue = null, status = LandTitleStatus.ISSUED)
val requestIssuanceState = RequestIssuanceState(titleId, ALICE.party, BOB.party, seller, RequestIssuanceStatus.PENDING)
val agreementState = LandAgreementState(titleId, buyer, seller, CHARLIE.party, BOB.party, LocalDate.now(), LocalDate.now().plusDays(14), 9.0, 1000.POUNDS, 50.POUNDS, null, 950.POUNDS, landTitleState.linearId.toString(), listOf(),TitleGuarantee.FULL ,AgreementStatus.CREATED)
val landTitleStateWithBuyerConveyancer = landTitleState.copy(participants = landTitleState.participants + agreementState.buyerConveyancer)