package com.hmlr.model

import net.corda.core.contracts.Amount
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.security.PublicKey
import java.time.Instant
import java.util.*

/**
 * Data class representing land title property details
 */
@CordaSerializable
data class LandTitleProperties(val address: Address,
                               val ownerConveyancer: Party,
                               val ownerLender:Party,
                               val owner: CustomParty)

/**
 * Data class representing land title address details
 */
@CordaSerializable
data class Address( val houseNumber:String,
                    val streetName: String,
                    val county: String,
                    val city: String,
                    val country: String,
                    val postalCode: String)

/**
 * Data class representing end-user details
 * Seller and Buyer in this context
 */
@CordaSerializable
data class CustomParty(val forename: String,
                       val surname: String,
                       val userID: String,
                       val address: Address,
                       val userType: UserType,
                       val email: String,
                       val phone: String,
                       val confirmationOfIdentity: Boolean,
                       val signature: ByteArray?,
                       val publicKey: PublicKey?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val customParty = other as CustomParty
        return forename == customParty.forename &&
                surname == customParty.surname &&
                userID == customParty.userID &&
                address == customParty.address &&
                userType == customParty.userType &&
                email == customParty.email &&
                phone == customParty.phone &&
                confirmationOfIdentity == customParty.confirmationOfIdentity &&
                java.util.Arrays.equals(signature, customParty.signature) &&
                publicKey!!.equals(customParty.publicKey)
    }
}

@CordaSerializable
data class Charge(val date: Instant,
                  val lender: Party,
                  val amount: Amount<Currency>): Comparable<Charge> {

    override fun compareTo(other: Charge): Int {
        if(date > other.date)
            return 1
        else if (date < other.date)
            return -1
        else return 0
    }
}

@CordaSerializable
open class Restriction(val restrictionId: String,
                       val restrictionText: String,
                       val consentingParty: Party,
                       val action: ActionOnRestriction,
                       val consentGiven: Boolean): Comparable<Restriction> {
        override fun compareTo(other: Restriction): Int {
            if(restrictionId > other.restrictionId)
                return 1
            else if (restrictionId < other.restrictionId)
                return -1
            else return 0
        }
}


@CordaSerializable
class ChargeRestriction( restrictionId: String,
                         restrictionText: String,
                         consentingParty: Party,
                         action: ActionOnRestriction,
                         consentGiven: Boolean,
                         val charge: Charge) : Restriction(restrictionId, restrictionText, consentingParty, action, consentGiven) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val chargeRestriction = other as ChargeRestriction
        return charge.equals(chargeRestriction.charge) &&
                restrictionId.equals(chargeRestriction.restrictionId) &&
                restrictionText.equals(chargeRestriction.restrictionText) &&
                consentingParty.equals(chargeRestriction.consentingParty) &&
                action.equals(chargeRestriction.action) &&
                consentGiven.equals(chargeRestriction.consentGiven)
    }
}

@CordaSerializable
enum class ActionOnRestriction {
    NO_ACTION,
    DISCHARGE,
    ADD_RESTRICTION
}

@CordaSerializable
enum class ChargeType {
    MORTGAGE
}

/**
 * Enum class representing Land title type
 */
@CordaSerializable
enum class TitleType {
    PARTIAL,
    WHOLE
}

/**
 * Enum class representing user type
 */
@CordaSerializable
enum class UserType {
    INDIVIDUAL,
    COMPANY,
    OVERSEAS_COMPANY,
    NGO
}


/**
 * Enum class representing land title status
 */
@CordaSerializable
enum class LandTitleStatus {
    ISSUED,
    ASSIGN_BUYER_CONVEYANCER,
    TRANSFERRED
}

/**
 * Enum class representing Request Issuance status
 */
@CordaSerializable
enum class RequestIssuanceStatus {
    PENDING,
    APPROVED,
    TITLE_ALREADY_ISSUED,
    FAILED
}

/**
 * Enum class representing Agreement state status
 */
@CordaSerializable
enum class AgreementStatus {
    CREATED,
    APPROVED,
    SIGNED,
    COMPLETED,
    TRANSFERRED
}

/**
 * Enum class representing land title guarantee
 */
@CordaSerializable
enum class TitleGuarantee {
    LIMITED,
    FULL
}

/**
 * Enum class representing DTC consent status
 */
@CordaSerializable
enum class DTCConsentStatus {
    ISSUED,
    REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE,
    CONSENT_FOR_DISCHARGE,
    ASSIGN_BUYER_CONVEYANCER,
    CONSENT_FOR_NEW_CHARGE,
}

/**
 * Enum class representing Payment Confirmation status
 */
@CordaSerializable
enum class PaymentConfirmationStatus {
    ISSUED,
    REQUEST_FOR_PAYMENT,
    CONFIRM_PAYMENT_RECEIVED_IN_ESCROW,
    CONFIRM_FUNDS_RELEASED
}