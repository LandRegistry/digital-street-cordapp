package com.hmlr.contracts

import com.hmlr.*
import com.hmlr.model.*
import com.hmlr.states.LandTitleState
import net.corda.finance.POUNDS
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class LandTitleIssueTest {

    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun mustIncludeIssueLandTitleCommand() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceApproveTest.DummyCommand())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyOneInputState() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            // Remove an input state
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            // Add an input state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyTwoOutputStates() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            // Add an output state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            // Remove an output state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.verifies()
            }
        }
    }

    @Test
    fun statusOfIssueTransactionShouldBeSetToIssued() {
        ledgerServices.ledger{
            transaction {
                val location = Address("A1-S2","GreenBank Road","Devon","Plymouth", "UK", "PL6 5ZD")
                val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com","9422327871",true, null, sellerPublicKey))
                val landTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, titleType = TitleType.WHOLE, lastSoldValue = null, status = LandTitleStatus.TRANSFERRED)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.verifies()
            }
        }
    }

    @Test
    fun issuedLandAssetShouldNotHaveOfferPrice() {
        ledgerServices.ledger {
            transaction {
                val location = Address("A1-S2","GreenBank Road","Devon","Plymouth", "UK", "PL6 5ZD")
                val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com","9422327871",true, null, sellerPublicKey))
                val landTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, titleType = TitleType.WHOLE, lastSoldValue = 100.POUNDS, status = LandTitleStatus.ISSUED)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(landTitleState.titleIssuer.owningKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                this .fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.verifies()
            }
        }
    }

    @Test
    fun issuerMustSignIssueLandTitleIssuanceTransaction() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.verifies()
            }
        }
    }

    @Test
    fun sellerIdentityMustBeVerified() {
        ledgerServices.ledger {

            transaction {
                val location = Address("A1-S2","GreenBank Road","Devon","Plymouth", "UK", "PL6 5ZD")
                val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com","9422327871",false, null, sellerPublicKey))
                val landTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, titleType = TitleType.WHOLE, lastSoldValue = null, status = LandTitleStatus.ISSUED)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.verifies()
            }
        }
    }

    @Test
    fun issuedLandTitleNumberShouldMatchWithRequestedTitleNumber() {
        ledgerServices.ledger {

            transaction {
                val location = Address("A1-S2","GreenBank Road","Devon","Plymouth", "UK", "PL6 5ZD")
                val prop = LandTitleProperties(location, BOB.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com","9422327871",true, null, sellerPublicKey))
                val landTitleState = LandTitleState(titleID = "212123131", landTitleProperties = prop, titleIssuer = ALICE.party, titleType = TitleType.WHOLE, lastSoldValue = null, status = LandTitleStatus.ISSUED)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.verifies()
            }
        }
    }
}