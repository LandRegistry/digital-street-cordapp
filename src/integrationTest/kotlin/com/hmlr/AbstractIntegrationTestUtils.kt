package com.hmlr

import com.hmlr.model.*
import net.corda.core.crypto.Crypto
import net.corda.core.identity.CordaX500Name
import java.security.PrivateKey
import java.time.Instant
import java.time.LocalDate

abstract class AbstractIntegrationTestUtils {

    val HMLR = CordaX500Name("HMLR", "Plymouth", "GB")
    val ConveyancerA = CordaX500Name("Conveyancer1", "Plymouth", "GB")
    val ConveyancerB = CordaX500Name("Conveyancer2", "Plymouth", "GB")
    val LenderA = CordaX500Name("Lender1", "Plymouth", "GB")
    val SettlingParty = CordaX500Name("SettlingParty", "Plymouth", "GB")
    val RevenueAndCustomParty = CordaX500Name("HMRC", "Plymouth", "GB")
    val buyerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val sellerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val buyerPublicKey = buyerKeys.public
    val buyerPrivateKey = buyerKeys.private
    val sellerPublicKey = sellerKeys.public
    val sellerPrivateKey = sellerKeys.private
    val titleId = "ZQV888860"
    val caseRefNum = "ABCD123"
    val location = Address("10", "Digital Street", "Bristol", "Bristol", "England", "BS2 8EN")
    val seller = CustomParty("Lisa", "White", "1", location, UserType.INDIVIDUAL, "lisa.white@example.com", "+447700900354", true, signature = null, publicKey = sellerPublicKey)
    val buyer = CustomParty("David", "Jones", "125464", location, UserType.INDIVIDUAL, "buyer@example.com", "+447700900354", true, signature = null, publicKey = buyerPublicKey)
    var completionDate: Instant? = null
    val creationDate = LocalDate.now()
    val restrictionText = "No disposition of the registered estate by the proprietor of the registered estate is to be registered"

    protected fun sign(message: String, key: PrivateKey): ByteArray {
        return Crypto.doSign(Crypto.RSA_SHA256, key, message.toByteArray())
    }
}