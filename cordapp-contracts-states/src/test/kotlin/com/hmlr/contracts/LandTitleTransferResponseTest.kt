package com.hmlr.contracts

import com.hmlr.*
import com.hmlr.model.*
import com.hmlr.states.LandTitleState
import net.corda.core.crypto.Crypto
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test
import java.security.PrivateKey

class LandTitleTransferResponseTest {

    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))
    private fun sign(message: String, key: PrivateKey): ByteArray {
        return Crypto.doSign(Crypto.RSA_SHA256, key, message.toByteArray())
    }

    @Test
    fun mustIncludeLandTitleTransferResponseCommand() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val inputprop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true,sign("12345", buyerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, buyerPublicKey), CHARLIE.party)
        val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), null, null)
        val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
        val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)

        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleTransferRequestTest.DummyCommand())
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyOneOutputState() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val inputprop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true,sign("12345", buyerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, buyerPublicKey), CHARLIE.party)
        val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), null, null)
        val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
        val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)

        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.verifies()

            }
        }
    }

    @Test
    fun mustIncludeExactlyOneInputState() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val inputprop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true,sign("12345", buyerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, buyerPublicKey), CHARLIE.party)
        val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), null, null)
        val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
        val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)

        ledgerServices.ledger {
            transaction {
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.verifies()
            }
        }
    }

    @Test
    fun statusOfConsumeLandTitleMustBePending() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val inputprop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true,sign("12345", buyerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, buyerPublicKey), CHARLIE.party)
        val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), null, null)
        ledgerServices.ledger {
            transaction {
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.ISSUED, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }
            transaction {
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.verifies()
            }
        }
    }

    @Test
    fun statusOfUpdatedLandShouldBeTransferred() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val inputprop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true,sign("12345", buyerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, buyerPublicKey), CHARLIE.party)
        val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), null, null)
        ledgerServices.ledger {
            transaction {
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.ISSUED, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }
            transaction {
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.PENDING_BUYER_APPROVAL)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }
            transaction {
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.verifies()
            }
        }
    }

    @Test
    fun correctOwnerInOutputStateShouldBeSet() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val inputprop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true,sign("12345", buyerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, buyerPublicKey), CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), null, null)
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }
            transaction {
                val outputprop = LandTitleProperties(location, BOB.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), null, null)
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }
            transaction {
                val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), null, null)
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.verifies()
            }
        }
    }

    @Test
    fun buyerAndAcceptingPartyMustBeNull() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val inputprop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true,sign("12345", buyerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, buyerPublicKey), CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), null, CHARLIE.party)
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }

            transaction {
                val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), CHARLIE.party)
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }
            transaction {
                val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), null, null)
                val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.verifies()
            }
        }
    }

    @Test
    fun onlyTheBuyerCanApproveTheRequest() {
        val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
        val inputprop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true,sign("12345", buyerPrivateKey), sellerPublicKey), CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, null, buyerPublicKey), CHARLIE.party)
       val inputlandTitleState = LandTitleState(titleID = "12345", landTitleProperties = inputprop, titleIssuer = ALICE.party, referenceDocuments = listOf(), titleType = TitleType.WHOLE, lastSoldValue = null, status = Status.PENDING_BUYER_APPROVAL, participants = listOf(ALICE.party, BOB.party, CHARLIE.party))

        ledgerServices.ledger {
            transaction {
                val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", wrongSellerPrivateKey), wrongSellerPublicKey), null, null)
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.fails()
            }
            transaction {
                val outputprop = LandTitleProperties(location, CHARLIE.party, CustomParty("Alex", "Gomes", "234332", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, sign("12345", buyerPrivateKey), buyerPublicKey), null, null)
                val outputLandtitleState = inputlandTitleState.copy(landTitleProperties = outputprop, status = Status.TRANSFERRED)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, inputlandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandtitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.LandTitleTransferResponse())
                this.verifies()
            }
        }
    }
}