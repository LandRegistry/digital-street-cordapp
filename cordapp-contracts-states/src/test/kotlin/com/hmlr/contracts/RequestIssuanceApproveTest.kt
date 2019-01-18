package com.hmlr.contracts

import com.hmlr.*
import com.hmlr.model.*
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.contracts.DummyState
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class RequestIssuanceApproveTest {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun mustIncludeApproveRequestAndIssueLandTitleCommands() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), DummyCommand())
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
    fun inputStateMustBeIssuanceRequest() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, DummyState())
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
        }
    }

    @Test
    fun inputIssuanceRequestStatusMustBePending() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            // Set input status to 'APPROVED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            // Set input status to 'REJECTED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.TITLE_ALREADY_ISSUED))
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            // Set input status to 'FAILED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED))
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
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
    fun outputStatesMustIncludeIssuanceRequest() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, DummyState())
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
    fun requestIssuanceStateMustBeSignedByTitleIssuer() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
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
    fun outputIssuanceRequestStatusMustBeApproved() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            // Set output status to 'PENDING'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                this.fails()
            }
            // Set output status to 'REJECTED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.TITLE_ALREADY_ISSUED))
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

}