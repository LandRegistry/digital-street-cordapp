package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.*
import com.hmlr.states.LandTitleState
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class IssueProposedChargeAndRestrictionTests : AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include Issue proposed charge and restriction Command`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), IssueProposedChargeAndRestrictionTests.DummyCommand())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
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
    fun `must Include Exactly One Input State `() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            // Remove an input state
            transaction {
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
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
    fun `must Include Exactly Three Output States`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
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
    fun `status Of Issue Proposed charge and restriction state Should Be Set To Issued`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.CONSENT_FOR_DISCHARGE))
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
    fun `list of charges and restrictions must be equal in title state as well as proposed charges and restriction state`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(charges = setOf()))
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(restrictions = setOf()))
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(charges = setOf() ,restrictions = setOf()))
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
    fun `restrictions must not have any consents when issued on the ledger`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(addNewChargeConsented = true))
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }

            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(dischargeConsented = true))
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }

            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(dischargeConsented = true, addNewChargeConsented = true))
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }

            transaction {
                val restriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.ADD_RESTRICTION, false, charge)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(restrictions = setOf(restriction)))
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
    fun `restrictions should have correct consenting parties`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                val restriction = ChargeRestriction("CBCR", restrictionText, LENDER2.party, ActionOnRestriction.NO_ACTION, false, charge)
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(restrictions = setOf(restriction)))
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