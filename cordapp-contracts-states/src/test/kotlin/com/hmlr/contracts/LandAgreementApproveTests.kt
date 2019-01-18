package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.*
import net.corda.core.contracts.TypeOnlyCommandData

import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class LandAgreementApproveTests : AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include ApproveLandAgreement Command`() {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementApproveTests.DummyCommand())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Input State`() {
        ledgerServices.ledger {
            transaction {
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `input AgreementStatus Must Be Created`() {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, isMortgageTermsAdded = true))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Output State`() {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By Buyer Conveyancer` () {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey, BOB.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `output Agreement Status Must Be Approved` () {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.CREATED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                this.verifies()
            }
        }
    }
}