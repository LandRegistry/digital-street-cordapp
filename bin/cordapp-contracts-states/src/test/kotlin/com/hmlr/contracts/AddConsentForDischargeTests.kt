package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.ActionOnRestriction
import com.hmlr.model.ChargeRestriction
import com.hmlr.model.DTCConsentStatus
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.contracts.UniqueIdentifier
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class AddConsentForDischargeTests: AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include Add Consent For Discharge Command`() {
        val inputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val inputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(inputChargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)

        val outputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(outputChargeRestriction), status = DTCConsentStatus.CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), AddConsentForDischargeTests.DummyCommand())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Input State`() {
        val inputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val inputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(inputChargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)

        val outputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(outputChargeRestriction), status = DTCConsentStatus.CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            transaction {
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Output State`() {
        val inputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val inputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(inputChargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)

        val outputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(outputChargeRestriction), status = DTCConsentStatus.CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By Consenting Party`() {
        val inputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val inputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(inputChargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)

        val outputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(outputChargeRestriction), status = DTCConsentStatus.CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER2.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.verifies()

            }
        }
    }

    @Test
    fun `status of output state Must Be CONSENT_FOR_DISCHARGE`() {
        val inputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val inputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(inputChargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)

        val outputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(outputChargeRestriction), status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(status = DTCConsentStatus.CONSENT_FOR_DISCHARGE))
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.verifies()

            }
        }
    }

    @Test
    fun `Restrictions list size should remain the same`() {
        val inputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val inputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(inputChargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)

        val outputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(outputChargeRestriction), status = DTCConsentStatus.CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(restrictions = setOf()))
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.verifies()

            }
        }
    }

    @Test
    fun `Check for input and output states integrity`() {
        val inputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val inputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(inputChargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)

        val outputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(outputChargeRestriction), status = DTCConsentStatus.CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            // change charges list
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(charges = setOf()))
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }

            // change title id
            transaction {
                // change charges list
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(titleID = "323232"))
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }

            // change addNewChargeConsented
            transaction {
                // change charges list
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(addNewChargeConsented = true))
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }

            // change dischargeConsented
            transaction {
                // change charges list
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(dischargeConsented = true))
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }

            // change linearId
            transaction {
                // change charges list
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(linearId = UniqueIdentifier()))
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }

            // change participants
            transaction {
                // change charges list
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(participants = listOf(LENDER1.party)))
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }

            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `Fields Except ConsentGiven In The Restrictions List Should Not Be changed`() {
        ledgerServices.ledger {
            transaction {
                val inputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
                val inputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(inputChargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
                val outputChargeRestriction = ChargeRestriction("CBCR1", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, true, charge)
                val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(outputChargeRestriction), status = DTCConsentStatus.CONSENT_FOR_DISCHARGE)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }
            transaction {
                val inputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
                val inputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(inputChargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
                val outputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, true, charge)
                val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(outputChargeRestriction), status = DTCConsentStatus.CONSENT_FOR_DISCHARGE)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `Charge In The ChargeRestriction List Should Not Be Changed `() {
        ledgerServices.ledger {
            transaction {
                val inputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
                val inputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(inputChargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
                val outputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, true, charge.copy(lender = LENDER2.party))
                val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(outputChargeRestriction), status = DTCConsentStatus.CONSENT_FOR_DISCHARGE)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.fails()
            }
            transaction {
                val inputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
                val inputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(inputChargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
                val outputChargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, true, charge)
                val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(outputChargeRestriction), status = DTCConsentStatus.CONSENT_FOR_DISCHARGE)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, inputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge())
                this.verifies()
            }
        }
    }
}