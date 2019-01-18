package com.hmlr

import com.hmlr.model.*
import com.hmlr.states.LandTitleState
import com.hmlr.states.RequestIssuanceState
import net.corda.core.crypto.Crypto
import net.corda.core.identity.CordaX500Name
import net.corda.testing.core.TestIdentity

val ALICE = TestIdentity(CordaX500Name(organisation = "Alice", locality = "TestLand", country = "US"))
val BOB = TestIdentity(CordaX500Name(organisation = "Bob", locality = "TestCity", country = "US"))
var CHARLIE = TestIdentity(CordaX500Name(organisation = "Charlie", locality = "TestVillage", country = "US"))
val titleId = "12345"
val buyerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
val sellerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
val wrongSellerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)

val buyerPublicKey = buyerKeys.public
val buyerPrivateKey = buyerKeys.private

val sellerPublicKey = sellerKeys.public
val sellerPrivateKey = sellerKeys.private

val wrongSellerPublicKey = wrongSellerKeys.public
val wrongSellerPrivateKey = wrongSellerKeys.private

val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, signature = null, publicKey = sellerPublicKey), null, null)
val seller = CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, signature = null, publicKey = sellerPublicKey)
val landTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.ISSUED)
val requestIssuanceState = RequestIssuanceState(titleId, ALICE.party, BOB.party, seller, RequestIssuanceStatus.PENDING)