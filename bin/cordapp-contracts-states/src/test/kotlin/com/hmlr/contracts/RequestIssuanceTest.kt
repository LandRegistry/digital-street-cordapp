package com.hmlr.contracts

import com.hmlr.*
import com.hmlr.model.*
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class RequestIssuanceTest {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun mustIncludeRequestIssuanceCommand() {
        ledgerServices.ledger {
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), DummyCommand())
                this.fails()
            }
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun mustNotIncludeInputState() {
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyOneOutputState() {
        ledgerServices.ledger {
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun titleIssuerAndConveyancerMustBeDifferent() {
        ledgerServices.ledger {
            transaction {
                val outputRequestIssuanceState = requestIssuanceState.copy(titleIssuer = requestIssuanceState.sellerConveyancer)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputRequestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun requestMustOnlyBeSignedByConveyancer() {
        ledgerServices.ledger {
            // Change the signer
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            // Add a signer
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(ALICE.publicKey, BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun conveyancerAndTitleIssuerMustBeOnlyParticipants() {
        ledgerServices.ledger {
            transaction {
                val outputRequestIssuanceState = requestIssuanceState.copy(participants = requestIssuanceState.participants + CHARLIE.party)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputRequestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun outputStatusMustBePending() {
        ledgerServices.ledger {
            // Change status to approved
            transaction {
                val outputRequestIssuanceState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputRequestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            // Change status to rejected
            transaction {
                val outputRequestIssuanceState = requestIssuanceState.copy(status = RequestIssuanceStatus.TITLE_ALREADY_ISSUED)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputRequestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

}