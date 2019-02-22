package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.DTCConsentStatus
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.utilities.seconds
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class AssignBuyerConveyancerToPropsedChargeAndRestrictionStateTests: AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include Assign Buyer Conveyancer Command`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), AssignBuyerConveyancerToPropsedChargeAndRestrictionStateTests.DummyCommand())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly Two Input States`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly Four Output States`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun `status of output must be ASSIGN_BUYER_CONVEYANCER`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By The Owner Conveyancer`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Sent To Correct Conveyancer For Them To Add New Charge`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = LENDER1.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(buyerConveyancer = CHARLIE.party))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }
}