package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.DTCConsentStatus
import com.hmlr.model.LandTitleStatus
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.utilities.seconds
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class LandTitleAssignBuyerConveyancerTests : AbstractContractsStatesTestUtils() {

    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))
    class DummyCommand : TypeOnlyCommandData()

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
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                command(listOf(BOB.publicKey), AssignBuyerConveyancerToPropsedChargeAndRestrictionStateTests.DummyCommand())
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
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
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
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
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
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Four Output States`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
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
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun `buyer Conveyancer Must Be Participant`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        val invalidOutputLandTitleState = landTitleState.copy(participants = landTitleState.participants + ALICE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, invalidOutputLandTitleState)
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
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
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By Owners Conveyancer`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
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
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
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
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
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
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }
}
