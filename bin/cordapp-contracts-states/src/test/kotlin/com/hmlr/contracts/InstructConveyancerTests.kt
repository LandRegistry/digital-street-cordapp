package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class InstructConveyancerTests : AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include InstructConveyancer Command`() {
        ledgerServices.ledger {
            transaction {
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                command(listOf(ALICE.publicKey), InstructConveyancerTests.DummyCommand())
                this.fails()
            }
            transaction {
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                command(listOf(ALICE.publicKey), InstructConveyancerContract.Commands.CreateInstruction())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Not Include InputState`() {
        ledgerServices.ledger {
            transaction {
                input(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                command(listOf(ALICE.publicKey), InstructConveyancerTests.DummyCommand())
                this.fails()
            }
            transaction {
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                command(listOf(ALICE.publicKey), InstructConveyancerContract.Commands.CreateInstruction())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Output State`() {
        ledgerServices.ledger {
            transaction {
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                command(listOf(ALICE.publicKey), InstructConveyancerContract.Commands.CreateInstruction())
                this.fails()
            }
            transaction {
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                command(listOf(ALICE.publicKey), InstructConveyancerContract.Commands.CreateInstruction())
                this.verifies()
            }
        }
    }

    @Test
    fun `conveyancer Must Not Be Same As TitleIssuer`() {
        ledgerServices.ledger {
            transaction {
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState.copy(conveyancer = ALICE.party))
                command(listOf(ALICE.publicKey), InstructConveyancerContract.Commands.CreateInstruction())
                this.fails()
            }
            transaction {
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                command(listOf(ALICE.publicKey), InstructConveyancerContract.Commands.CreateInstruction())
                this.verifies()
            }
        }
    }

    @Test
    fun `owner Identity Must Be Identified`() {
        ledgerServices.ledger {
            transaction {
                val unidentifiedOwner = instructConveyancerState.user.copy(confirmationOfIdentity = false)
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState.copy(user = unidentifiedOwner))
                command(listOf(ALICE.publicKey), InstructConveyancerContract.Commands.CreateInstruction())
                this.fails()
            }
            transaction {
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                command(listOf(ALICE.publicKey), InstructConveyancerContract.Commands.CreateInstruction())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By TitleIssuer`() {
        ledgerServices.ledger {
            transaction {
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState.copy(titleIssuer = CHARLIE.party))
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.CreateInstruction())
                this.fails()
            }
            transaction {
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                command(listOf(BOB.publicKey), InstructConveyancerContract.Commands.CreateInstruction())
                this.fails()
            }
            transaction {
                output(InstructConveyancerContract.INSTRUCT_CONVEYANCER_CONTRACT_ID, instructConveyancerState)
                command(listOf(ALICE.publicKey), InstructConveyancerContract.Commands.CreateInstruction())
                this.verifies()
            }
        }
    }
}