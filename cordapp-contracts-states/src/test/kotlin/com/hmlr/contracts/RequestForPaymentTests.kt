package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.AgreementStatus
import com.hmlr.model.PaymentConfirmationStatus
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class RequestForPaymentTests: AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include RequestForPayment Command`() {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), RequestForPaymentTests.DummyCommand())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
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
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
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
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
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
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(ALICE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.verifies()
            }
        }
    }

    @Test
    fun `output Payment State Status Must Be Request For Payment` () {
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.ISSUED, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.CONFIRM_FUNDS_RELEASED, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT, landAgreementStateLinearId = agreementState.linearId.toString()))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.ApproveSalesAgreement())
                command(listOf(CHARLIE.publicKey), PaymentConfirmationContract.Commands.RequestForPayment())
                this.verifies()
            }
        }
    }
}