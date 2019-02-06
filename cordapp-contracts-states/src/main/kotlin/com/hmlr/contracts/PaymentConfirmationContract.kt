package com.hmlr.contracts

import com.hmlr.model.PaymentConfirmationStatus
import com.hmlr.states.*
import com.hmlr.utils.checkPropertyInvariants
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

class PaymentConfirmationContract: Contract {

    /**
     * Define Contract ID
     */
    companion object {
        @JvmStatic
        val PAYMENT_CONFIRMATION_CONTRACT_ID = PaymentConfirmationContract::class.java.name!!
    }

    /**
     * List of all commands classes supported by the [LandTitleContract]
     */
    interface Commands: CommandData {
        class IssuePaymentConfirmation: TypeOnlyCommandData(), Commands
        class RequestForPayment: TypeOnlyCommandData(), Commands
        class ConfirmPaymentReceived: TypeOnlyCommandData(), Commands
        class ConfirmFundsReleased: TypeOnlyCommandData(), Commands
    }

    /**
     * Pure [Boolean] function to verify the incoming transaction
     * @param tx [LedgerTransaction]
     * @return [Boolean]
     */
    override fun verify(tx: LedgerTransaction) {
        val commands = tx.commands.requireSingleCommand<PaymentConfirmationContract.Commands>()
        val setOfSigners = commands.signers.toSet()
        when (commands.value) {
            is PaymentConfirmationContract.Commands.IssuePaymentConfirmation -> verifyIssuePaymentConfirmation(tx, setOfSigners)
            is PaymentConfirmationContract.Commands.RequestForPayment -> verifyRequestForPayment(tx, setOfSigners)
            is PaymentConfirmationContract.Commands.ConfirmPaymentReceived -> verifyConfirmPaymentReceived(tx, setOfSigners)
            is PaymentConfirmationContract.Commands.ConfirmFundsReleased -> verifyConfirmFundsReleased(tx, setOfSigners)
            else -> throw IllegalArgumentException("Unrecognised Command")
        }
    }

    /**
     * validation logic for [Commands.IssuePaymentConfirmation] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyIssuePaymentConfirmation(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{

        "Two input should be consumed while creating payment confirmation state" using (tx.inputs.size == 2)
        val outputLandAgreementState = tx.outputsOfType<LandAgreementState>().single()
        val outputPaymentConfirmationState = tx.outputsOfType<PaymentConfirmationState>().single()

        "There should be exactly four output states" using (tx.outputs.size == 4)
        "Transaction must be signed by the seller's conveyancer" using (setOfSigners.size == 1 && setOfSigners.contains(outputLandAgreementState.sellerConveyancer.owningKey))
        "Linear id of the agreement state in payment confirmation state must match with the produced agreement state" using(outputLandAgreementState.linearId.toString() == outputPaymentConfirmationState.landAgreementStateLinearId)
        "Buyer, seller, title id and purchase price must match with agreement state" using(outputLandAgreementState.buyer.equals(outputPaymentConfirmationState.buyer) && outputLandAgreementState.seller.equals(outputPaymentConfirmationState.seller) && outputLandAgreementState.buyerConveyancer.equals(outputPaymentConfirmationState.buyerConveyancer)
                && outputLandAgreementState.titleID.equals(outputPaymentConfirmationState.titleID)
                && outputLandAgreementState.purchasePrice.equals(outputPaymentConfirmationState.purchasePrice))
        "Status must be set to REQUEST_FOR_PAYMENT" using(outputPaymentConfirmationState.status == PaymentConfirmationStatus.ISSUED)
        "Settling party, seller conveyancer and buyer conveyancer must be added to participants list" using(outputPaymentConfirmationState.participants.size == 3 && outputPaymentConfirmationState.participants.containsAll(listOf(outputPaymentConfirmationState.settlingParty, outputLandAgreementState.buyerConveyancer, outputLandAgreementState.sellerConveyancer)))
    }

    /**
     * validation logic for [Commands.RequestForPayment] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyRequestForPayment(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{

        "Two input should be consumed while requesting for payment" using (tx.inputs.size == 2)
        val inputPaymentConfirmationState = tx.inputsOfType<PaymentConfirmationState>().single()
        val outputPaymentConfirmationState = tx.outputsOfType<PaymentConfirmationState>().single()

        val inputAgreementState = tx.inputsOfType<LandAgreementState>().single()

        "There should be exactly two output state while requesting for payment" using (tx.outputs.size == 2)
        "Transaction must be signed by the buyer's conveyancer" using (setOfSigners.size == 1 && setOfSigners.contains(inputAgreementState.buyerConveyancer.owningKey))
        "Status of input payment confirmation state must be 'ISSUED'" using(inputPaymentConfirmationState.status == PaymentConfirmationStatus.ISSUED)
        "Status of output payment confirmation state must be set to 'REQUEST_FOR_PAYMENT'" using(outputPaymentConfirmationState.status == PaymentConfirmationStatus.REQUEST_FOR_PAYMENT)

        val invariantRestrictionProperties = setOf(
                PaymentConfirmationState::titleID,
                PaymentConfirmationState::seller,
                PaymentConfirmationState::buyer,
                PaymentConfirmationState::buyerConveyancer,
                PaymentConfirmationState::purchasePrice,
                PaymentConfirmationState::settlingParty,
                PaymentConfirmationState::linearId,
                PaymentConfirmationState::participants
        )
        "Mismatch in the input and output payment confirmation state" using(checkPropertyInvariants(inputPaymentConfirmationState, outputPaymentConfirmationState, invariantRestrictionProperties))
    }

    /**
     * validation logic for [Commands.ConfirmPaymentReceived] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyConfirmPaymentReceived(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{

        "One input should be consumed while creating payment confirmation state" using (tx.inputs.size == 1)
        val inputPaymentConfirmationState = tx.inputsOfType<PaymentConfirmationState>().single()
        val outputPaymentConfirmationState = tx.outputsOfType<PaymentConfirmationState>().single()

        "There should be exactly One output state" using (tx.outputs.size == 1)
        "Transaction must be signed by the settling party" using (setOfSigners.size == 1 && setOfSigners.contains(inputPaymentConfirmationState.settlingParty.owningKey))
        "Status of input payment confirmation state must be 'REQUEST_FOR_PAYMENT'" using(inputPaymentConfirmationState.status == PaymentConfirmationStatus.REQUEST_FOR_PAYMENT)
        "Status of output payment confirmation state must be set to 'CONFIRM_PAYMENT_RECEIVED_IN_ESCROW'" using(outputPaymentConfirmationState.status == PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW)

        val invariantRestrictionProperties = setOf(
                PaymentConfirmationState::titleID,
                PaymentConfirmationState::seller,
                PaymentConfirmationState::buyer,
                PaymentConfirmationState::buyerConveyancer,
                PaymentConfirmationState::purchasePrice,
                PaymentConfirmationState::settlingParty,
                PaymentConfirmationState::linearId,
                PaymentConfirmationState::participants
        )
        "Mismatch in the input and output payment confirmation state" using(checkPropertyInvariants(inputPaymentConfirmationState, outputPaymentConfirmationState, invariantRestrictionProperties))
    }

    /**
     * validation logic for [Commands.ConfirmFundsReleased] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyConfirmFundsReleased(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "Four input states must be consumed" using(tx.inputs.size == 4)
        "Four output states must be produced" using(tx.outputs.size == 4)
        val inputPaymentConfirmationState = tx.inputsOfType<PaymentConfirmationState>().single()
        val outputPaymentConfirmationState = tx.outputsOfType<PaymentConfirmationState>().single()
        val inputAgreementState = tx.inputsOfType<LandAgreementState>().single()
        val inputLandTitleState = tx.inputsOfType<LandTitleState>().single()

        "Transaction must be signed by both the conveyancers, title issuer and settling party" using(setOfSigners.size == 4 && setOfSigners.containsAll(listOf(inputAgreementState.sellerConveyancer.owningKey, inputAgreementState.buyerConveyancer.owningKey, inputLandTitleState.titleIssuer.owningKey, inputPaymentConfirmationState.settlingParty.owningKey)))
        "Status of input payment confirmation state must be 'CONFIRM_PAYMENT_RECEIVED_IN_ESCROW'" using(inputPaymentConfirmationState.status == PaymentConfirmationStatus.CONFIRM_PAYMENT_RECEIVED_IN_ESCROW)
        "Status of output payment confirmation state must be set to 'CONFIRM_FUNDS_RELEASED'" using(outputPaymentConfirmationState.status == PaymentConfirmationStatus.CONFIRM_FUNDS_RELEASED)
        val invariantRestrictionProperties = setOf(
                PaymentConfirmationState::titleID,
                PaymentConfirmationState::seller,
                PaymentConfirmationState::buyer,
                PaymentConfirmationState::buyerConveyancer,
                PaymentConfirmationState::purchasePrice,
                PaymentConfirmationState::settlingParty,
                PaymentConfirmationState::linearId,
                PaymentConfirmationState::participants
        )
        "Mismatch in the input and output payment confirmation state" using(checkPropertyInvariants(inputPaymentConfirmationState, outputPaymentConfirmationState, invariantRestrictionProperties))
    }
}