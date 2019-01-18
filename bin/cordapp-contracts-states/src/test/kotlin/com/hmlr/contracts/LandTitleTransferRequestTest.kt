package com.hmlr.contracts

import com.hmlr.*
import com.hmlr.model.*
import com.hmlr.states.LandTitleState
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.crypto.Crypto
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test
import java.security.PrivateKey

class LandTitleTransferRequestTest {

    class DummyCommand : TypeOnlyCommandData()

    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))
    private fun sign(message: String, key: PrivateKey): ByteArray {
        return Crypto.doSign(Crypto.RSA_SHA256, key, message.toByteArray())
    }

    @Test
    fun mustIncludeLandTitleTransferRequestCommand() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", sellerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, null), CHARLIE.party)
        val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.ISSUED)
        val outputLandtitleState = inputlandTitleState.copy(participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)

        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), DummyCommand())
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyOneOutputState() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", sellerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, null), CHARLIE.party)
        val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.ISSUED)
        val outputLandtitleState = inputlandTitleState.copy(participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)

        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.verifies()

            }
        }
    }

    @Test
    fun mustIncludeExactlyOneInputState() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", sellerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, null), CHARLIE.party)
        val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.ISSUED)
        val outputLandtitleState = inputlandTitleState.copy(participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)

        ledgerServices.ledger {
            transaction {
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.verifies()
            }
        }
    }

    @Test
    fun checkForInputAndOutputStateIntegrity() {
        //2 fields are allowed to change: Status and Participants list
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", sellerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, null), CHARLIE.party)
        val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.ISSUED)

        ledgerServices.ledger {
            // change titleID in the output
            transaction {
                val outputLandtitleState = inputlandTitleState.copy(titleID = "423432432", participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.fails()
            }
            // change issuer in the output
            transaction {
                val outputLandtitleState = inputlandTitleState.copy(titleIssuer = CHARLIE.party, participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.fails()
            }
            // change titleType in the output
            transaction {
                val outputLandtitleState = inputlandTitleState.copy(titleType = TitleType.PARTIAL, participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.fails()
            }
            // change owner in the output
            transaction {
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = inputlandTitleState.landTitleProperties.copy(owner = landTitleState.landTitleProperties.owner!!.copy(forename = "Alice")), participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.fails()
            }
            // dont change anything
            transaction {
                val outputLandtitleState = inputlandTitleState.copy(participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.verifies()
            }
        }
    }

    @Test
    fun landTitleMustNotBeInPendingState() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", sellerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, null), CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL)
                val outputLandtitleState = inputlandTitleState.copy(participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.fails()
            }

            transaction {
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.ISSUED)
                val outputLandtitleState = inputlandTitleState.copy(participants = inputlandTitleState.participants + CHARLIE.party, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.fails()
            }

            transaction {
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.ISSUED)
                val outputLandtitleState = inputlandTitleState.copy(participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.verifies()
            }
        }
    }

    @Test
    fun onlyOwnerCanInitiateTransferRequest() {
        ledgerServices.ledger {
            transaction {
                val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
                val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", wrongSellerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, null), CHARLIE.party)
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL)
                val outputLandtitleState = inputlandTitleState.copy(participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.fails()
            }

            transaction {
                val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
                val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", sellerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, null), CHARLIE.party)
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.ISSUED)
                val outputLandtitleState = inputlandTitleState.copy(participants = inputlandTitleState.participants + CHARLIE.party, status = Status.PENDING_BUYER_APPROVAL)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferRequest())
                this.verifies()
            }
        }
    }
}

