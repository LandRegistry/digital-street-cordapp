package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.*
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.utilities.seconds
import net.corda.finance.POUNDS
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class LandAgreementCreateTests : AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include Create LandAgreement Command`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString()))
                output(PaymentConfirmationContract.PAYMENT_CONFIRMATION_CONTRACT_ID, paymentConfirmationState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
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
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
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
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), PaymentConfirmationContract.Commands.IssuePaymentConfirmation())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By Seller Conveyancer` () {
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
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
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
    fun `seller Must Be Owner` () {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), seller = buyer))
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
    fun `seller Conveyancer Must Be Owner Conveyancer` () {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), sellerConveyancer = ALICE.party))
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
    fun `creationDate Cannot Be In The Past`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), creationDate = LocalDate.now().minusDays(14)))
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
    fun `completionDate Cannot Be In The Past`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), completionDate = LocalDate.now().atStartOfDay().minusDays(14).toInstant(ZoneOffset.UTC)))
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
    fun `completionDate Must Be After CreationDate`() {

        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), creationDate = LocalDate.now().plusDays(1), completionDate = LocalDate.now().atStartOfDay().minusDays(1).toInstant(ZoneOffset.UTC)))
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
    fun `titleState LinearId Must Not Change`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), titleStateLinearId = agreementState.linearId.toString()))
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
    fun `titleId Must Not Change`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), titleID = "12"))
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
    fun `deposit Must Be Less Than PurchasePrice`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), deposit = 1500.POUNDS))
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
    fun `balance Must Equal Purchase Price Minus Deposit`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), balance = 1000.POUNDS))
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
    fun `seller And Buyer Must Be Different` () {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), buyer = seller))
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
    fun `seller Conveyancer And Buyer Conveyancer Must Be Participants`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), participants = agreementState.participants + ALICE.party))
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
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(), participants = listOf(BOB.party)))
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
    fun `agreementStatus Must Be Created`() {
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(buyerConveyancer = CHARLIE.party, status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER, participants = proposedChargeOrRestrictionState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(),status = AgreementStatus.COMPLETED))
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
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(),status = AgreementStatus.APPROVED))
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
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(paymentConfirmationStateLinearId = paymentConfirmationState.linearId.toString(),status = AgreementStatus.SIGNED))
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
}