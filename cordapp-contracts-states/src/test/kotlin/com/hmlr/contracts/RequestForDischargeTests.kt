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

class RequestForDischargeTests : AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include Request For Discharge Command`() {
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), RequestForDischargeTests.DummyCommand())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Input State`() {
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Output State`() {
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By Owner Conveyancer`() {
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(LENDER1.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `status of output state Must Be REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE`() {
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE))
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `Restrictions list size should remain the same`() {
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
         ledgerServices.ledger {
            transaction {
                val outputProposedChargeAndRestrictionStateWithEmptyRestrictions = proposedChargeOrRestrictionState.copy(restrictions = setOf(), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionStateWithEmptyRestrictions)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }
            transaction {
                val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `Check for input and output states integrity`() {
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            // change charges list
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(charges = setOf()))
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }

            // change title id
            transaction {
                // change charges list
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(titleID = "323232"))
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }

            // change addNewChargeConsented
            transaction {
                // change charges list
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(addNewChargeConsented = true))
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }

            // change dischargeConsented
            transaction {
                // change charges list
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(dischargeConsented = true))
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }

            // change linearId
            transaction {
                // change charges list
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(linearId = UniqueIdentifier()))
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }

            // change participants
            transaction {
                // change charges list
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(participants = listOf(LENDER1.party)))
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }

            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `Fields Except ActionOnRestriction In The Restrictions List Should Not Be changed`() {
        val restriction = ChargeRestriction("CBCR", restrictionText, LENDER2.party, ActionOnRestriction.NO_ACTION, false, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(restriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }
            transaction {
                val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE))
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.verifies()
            }
        }
    }

    @Test
    fun `Action must be DISCHARGE In The Output Restrictions List`() {
         ledgerServices.ledger {
            transaction {
                val restriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.NO_ACTION, false, charge)
                val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(restriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }
            transaction {
                val restriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
                val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(restriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.verifies()
            }
        }
    }


    @Test
    fun `Charge In The ChargeRestriction List Should Not Be Changed `() {
        ledgerServices.ledger {
            transaction {
                val restriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge.copy(lender = LENDER2.party))
                val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(restriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.fails()
            }
            transaction {
                val restriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.DISCHARGE, false, charge)
                val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(restriction), status = DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState)
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.RequestForDischarge())
                this.verifies()
            }
        }
    }
}