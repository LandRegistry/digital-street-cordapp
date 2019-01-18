package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class InstructConveyancerAcceptTests : AbstractContractsStatesTestUtils(){

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include InstructConveyancerAccept Command`() {
        ledgerServices.ledger {
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(BOB.publicKey), InstructConveyancerAcceptTests.DummyCommand())
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
    fun `must Include Exactly One Input`() {
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
    fun `must Include Exactly One Output`() {
        ledgerServices.ledger {
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
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
    fun `must Be Signed By InstructedConveyancer`() {
        ledgerServices.ledger {
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState.copy(titleIssuer = CHARLIE.party))
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(ALICE.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.RequestIssuance())
                this.fails()
            }
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                command(listOf(ALICE.publicKey), InstructConveyancerContract.Commands.AcceptInstruction())
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
