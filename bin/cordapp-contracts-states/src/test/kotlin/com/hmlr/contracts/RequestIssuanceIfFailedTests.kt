package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.*
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.contracts.DummyState
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class RequestIssuanceIfFailedTests : AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include RequestIssuanceIfFailed Command`() {
        val inputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), DummyCommand())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One InputState`() {
        val inputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            // Remove an input state
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            // Add an input state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `inputState Must Be IssuanceRequest`() {
        val inputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, DummyState())
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `input IssuanceRequestStatus Must Be Failed`() {
        val inputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            // Set status to 'PENDING'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.PENDING))
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            // Set status to 'REJECTED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.TITLE_ALREADY_ISSUED))
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            // Set status to 'APPROVED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Output State`() {
        val inputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            // Remove an output state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            // Add an output state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `titleIssuer And Conveyancer Must Be Different`() {
        val inputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            transaction {
                val outputRequestIssuanceState = inputIssuanceRequestState.copy(titleIssuer = requestIssuanceState.sellerConveyancer)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputRequestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `request Must Only Be Signed By Conveyancer`() {
        val inputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            // Change the signer
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            // Add a signer
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey, ALICE.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `only conveyancer And TitleIssuer Must Be Participants`() {
        val inputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            transaction {
                val outputRequestIssuanceState = requestIssuanceState.copy(participants = requestIssuanceState.participants + CHARLIE.party)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputRequestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `output Status Must Be Pending`() {
        val inputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            // Set status to 'FAILED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            // Set status to 'REJECTED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.TITLE_ALREADY_ISSUED))
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            // Set status to 'APPROVED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuanceIfFailed())
                this.verifies()
            }
        }
    }

}