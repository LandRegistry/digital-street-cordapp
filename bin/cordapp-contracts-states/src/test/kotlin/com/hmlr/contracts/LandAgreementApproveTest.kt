package com.hmlr.contracts

import com.hmlr.*
import com.hmlr.model.*
import com.hmlr.states.LandAgreementState
import com.hmlr.states.LandTitleState
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.finance.POUNDS
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test
import net.corda.testing.contracts.DummyState

class LandAgreementApproveTest {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun mustIncludeApproveLandAgreementCommand() {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementApproveTest.DummyCommand())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyOneInputState() {
        ledgerServices.ledger {
            transaction {
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun inputAgreementStatusMustBeCreated() {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }


    @Test
    fun mustIncludeExactlyOneOutputState() {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun mustBeSignedByBuyerConveyancer () {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey, BOB.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun outputAgreementStatusMustBeApproved () {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.CREATED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }
}