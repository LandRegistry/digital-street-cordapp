package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.*
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class RequestIssuanceTests : AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include RequestIssuance Command`() {
        ledgerServices.ledger {
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), DummyCommand())
                this.fails()
            }
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Input State`() {
        ledgerServices.ledger {
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Output State`() {
        ledgerServices.ledger {
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun `titleIssuer And Conveyancer Must Be Different`() {
        ledgerServices.ledger {
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                val outputRequestIssuanceState = requestIssuanceState.copy(titleIssuer = requestIssuanceState.sellerConveyancer)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputRequestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun `request Must Only Be Signed By Conveyancer`() {
        ledgerServices.ledger {
            // Change the signer
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            // Add a signer
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(ALICE.publicKey, BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun `conveyancer And Title Issuer Must Be Only Participants`() {
        ledgerServices.ledger {
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                val outputRequestIssuanceState = requestIssuanceState.copy(participants = requestIssuanceState.participants + CHARLIE.party)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputRequestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun `output Status Must Be Pending`() {
        ledgerServices.ledger {
            // Change status to approved
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                val outputRequestIssuanceState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputRequestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            // Change status to rejected
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                val outputRequestIssuanceState = requestIssuanceState.copy(status = RequestIssuanceStatus.TITLE_ALREADY_ISSUED)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputRequestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun `titleId Must Be Equal In Consumed InstructionState And IssuanceState`() {
        ledgerServices.ledger {
            transaction {
                val inValidInstructConveyancerState = instructConveyancerState.copy(titleID = "000000")
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, inValidInstructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }

    @Test
    fun `Request IssuanceState Must Have Correct LinearId Of InstructionState`() {
        ledgerServices.ledger {
            transaction {
                val inValidRequestIssuanceState = requestIssuanceState.copy(instructionStateLinearID = "00000")
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, inValidRequestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.verifies()
            }
        }
    }
}