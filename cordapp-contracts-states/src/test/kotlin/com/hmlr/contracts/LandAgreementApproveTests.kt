package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.*
import com.hmlr.utils.BasicSDLT
import com.hmlr.utils.SDLTCalculator
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
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                command(listOf(CHARLIE.publicKey), LandAgreementApproveTests.DummyCommand())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly Two Input State`() {
        ledgerServices.ledger {
            transaction {
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.verifies()
            }
        }
    }

    @Test
    fun `input AgreementStatus Must Be Created`() {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly Two Output State`() {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By Buyer Conveyancer` () {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(ALICE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.verifies()
            }
        }
    }

    @Test
    fun `output Agreement Status Must Be Approved` () {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.CREATED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.verifies()
            }
        }
    }
}