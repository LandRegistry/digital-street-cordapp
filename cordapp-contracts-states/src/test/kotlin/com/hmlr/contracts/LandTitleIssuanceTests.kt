package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.*
import com.hmlr.states.LandTitleState
import net.corda.finance.POUNDS
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class LandTitleIssuanceTests : AbstractContractsStatesTestUtils() {

    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include Issue Land Title Command`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceApproveTests.DummyCommand())
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
    fun `must Include Exactly One Input State `() {
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
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.verifies()
            }
        }
    }

    @Test
    fun `status Of Issue Transaction Should Be Set To Issued`() {
        ledgerServices.ledger{
            transaction {
                val location = Address("A1-S2","GreenBank Road","Devon","Plymouth", "UK", "PL6 5ZD")
                val prop = LandTitleProperties(location, BOB.party, LENDER1.party ,CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com","9422327871",true, null, sellerPublicKey))
                val landTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, titleType = TitleType.WHOLE, lastSoldValue = null, status = LandTitleStatus.TRANSFERRED, charges = setOf(charge), restrictions = setOf(chargeRestriction), proposedChargeOrRestrictionLinearId = proposedChargeOrRestrictionState.linearId.toString())
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
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
    fun `issued Land Asset Should Not Have Offer Price`() {
        ledgerServices.ledger {
            transaction {
                val location = Address("A1-S2","GreenBank Road","Devon","Plymouth", "UK", "PL6 5ZD")
                val prop = LandTitleProperties(location, BOB.party, LENDER1.party ,CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com","9422327871",true, null, sellerPublicKey))
                val landTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, titleType = TitleType.WHOLE, lastSoldValue = 100.POUNDS, status = LandTitleStatus.ISSUED, charges = setOf(charge), restrictions = setOf(chargeRestriction), proposedChargeOrRestrictionLinearId = proposedChargeOrRestrictionState.linearId.toString())
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(landTitleState.titleIssuer.owningKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this .fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
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
    fun `issuer Must Sign Issue Land Title Issuance Transaction`() {
        val outputIssuanceRequestState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
        ledgerServices.ledger {
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, outputIssuanceRequestState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.IssueLandTitle())
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
    fun `seller Identity Must Be Verified`() {
        ledgerServices.ledger {

            transaction {
                val location = Address("A1-S2","GreenBank Road","Devon","Plymouth", "UK", "PL6 5ZD")
                val prop = LandTitleProperties(location, BOB.party, LENDER1.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com","9422327871",false, null, sellerPublicKey))
                val landTitleState = LandTitleState(titleID = "12345", landTitleProperties = prop, titleIssuer = ALICE.party, titleType = TitleType.WHOLE, lastSoldValue = null, status = LandTitleStatus.ISSUED, charges = setOf(charge), restrictions = setOf(chargeRestriction), proposedChargeOrRestrictionLinearId = proposedChargeOrRestrictionState.linearId.toString())
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
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
    fun `issued Land Title Number Should Match With Requested Title Number`() {
        ledgerServices.ledger {

            transaction {
                val location = Address("A1-S2","GreenBank Road","Devon","Plymouth", "UK", "PL6 5ZD")
                val prop = LandTitleProperties(location, BOB.party, LENDER1.party, CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com","9422327871",true, null, sellerPublicKey))
                val landTitleState = LandTitleState(titleID = "212123131", landTitleProperties = prop, titleIssuer = ALICE.party, titleType = TitleType.WHOLE, lastSoldValue = null, status = LandTitleStatus.ISSUED, charges = setOf(charge), restrictions = setOf(chargeRestriction), proposedChargeOrRestrictionLinearId = proposedChargeOrRestrictionState.linearId.toString())
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                command(listOf(ALICE.publicKey), RequestIssuanceContract.Commands.ApproveRequest())
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(ALICE.publicKey), ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction())
                this.fails()
            }
            transaction {
                input(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState)
                output(RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID, requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED))
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