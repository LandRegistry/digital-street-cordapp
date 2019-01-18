package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.*
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.contracts.DummyState
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class RequestIssuanceFailedTests : AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include Issuance Failed Command`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), DummyCommand())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One InputState`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            // Remove an input state
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            // Add an input state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `inputState Must Be IssuanceRequest`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, DummyState())
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `input IssuanceRequestStatus Must Be Pending`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            // Set input status to 'APPROVED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            // Set input status to 'REJECTED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.TITLE_ALREADY_ISSUED))
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            // Set input status to 'FAILED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED))
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Output State`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            // Remove an output state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            // Add an output state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `output State Must Be IssuanceRequest`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, DummyState())
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `requestIssuanceState Must Be Signed By TitleIssuer`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.verifies()
            }
        }
    }

    @Test
    fun `output IssuanceRequestStatus Must Be Failed`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
        ledgerServices.ledger {
            // Set output status to 'APPROVED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState.copy(status = RequestIssuanceStatus.APPROVED))
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            // Set input status to 'REJECTED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState.copy(status = RequestIssuanceStatus.TITLE_ALREADY_ISSUED))
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            // Set input status to 'PENDING'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState.copy(status = RequestIssuanceStatus.PENDING))
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.IssuanceFailed())
                this.verifies()
            }
        }
    }
}