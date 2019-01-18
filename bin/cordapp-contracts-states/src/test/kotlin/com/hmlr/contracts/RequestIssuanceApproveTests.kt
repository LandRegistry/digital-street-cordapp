package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.*
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.contracts.DummyState
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class RequestIssuanceApproveTests : AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include ApproveRequest And IssueLandTitle Commands`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), DummyCommand())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Input State`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            // Remove an input state
            transaction {
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            // Add an input state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.verifies()
            }
        }
    }

    @Test
    fun `inputState Must Be IssuanceRequest`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, DummyState())
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
        }
    }

    @Test
    fun `input IssuanceRequestStatus Must Be Pending`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            // Set input status to 'APPROVED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            // Set input status to 'REJECTED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.TITLE_ALREADY_ISSUED))
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            // Set input status to 'FAILED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED))
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly Two Output States`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            // Add an output state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            // Remove an output state
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.verifies()
            }
        }
    }

    @Test
    fun `output States Must Include IssuanceRequest`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, DummyState())
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.verifies()
            }
        }
    }

    @Test
    fun `request IssuanceState Must Be Signed By TitleIssuer`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(BOB.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.verifies()
            }
        }
    }

    @Test
    fun ` output IssuanceRequestStatus Must Be Approved`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            // Set output status to 'PENDING'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            // Set output status to 'REJECTED'
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.TITLE_ALREADY_ISSUED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.verifies()
            }
        }
    }

}