package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.PaymentConfirmationStatus
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class ConfirmPaymentReceivedInEscrowTests: AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include ConfirmPaymentReceived Command`() {
        ledgerServices.ledger {
            transaction {
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW))
                command(SETTLING_PARTY.party.owningKey, ConfirmPaymentReceivedInEscrowTests.DummyCommand())
                this.fails()
            }
            transaction {
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW))
                command(SETTLING_PARTY.party.owningKey, PaymentConfirmationContract.Commands.ConfirmPaymentReceived())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Input State`() {
        ledgerServices.ledger {
            transaction {
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW))
                command(SETTLING_PARTY.party.owningKey, PaymentConfirmationContract.Commands.ConfirmPaymentReceived())
                this.fails()
            }
            transaction {
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW))
                command(SETTLING_PARTY.party.owningKey, PaymentConfirmationContract.Commands.ConfirmPaymentReceived())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Output State`() {
        ledgerServices.ledger {
            transaction {
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW))
                command(SETTLING_PARTY.party.owningKey, PaymentConfirmationContract.Commands.ConfirmPaymentReceived())
                this.fails()
            }
            transaction {
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW))
                command(SETTLING_PARTY.party.owningKey, PaymentConfirmationContract.Commands.ConfirmPaymentReceived())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By Settling Party`() {
        ledgerServices.ledger {
            transaction {
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW))
                command(ALICE.party.owningKey, PaymentConfirmationContract.Commands.ConfirmPaymentReceived())
                this.fails()
            }
            transaction {
                input(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.REQUEST_FOR_PAYMENT))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState.copy(landAgreementStateLinearId = agreementState.linearId.toString(), status = PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW))
                command(SETTLING_PARTY.party.owningKey, PaymentConfirmationContract.Commands.ConfirmPaymentReceived())
                this.verifies()
            }
        }
    }
}