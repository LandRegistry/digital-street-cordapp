package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.ActionOnRestriction
import com.hmlr.model.Charge
import com.hmlr.model.ChargeRestriction
import com.hmlr.model.DTCConsentStatus
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.finance.POUNDS
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test
import java.time.Instant

class AddConsentForNewChargeTests: AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include Add Consent For New Charge Command`() {
        val charge = Charge(date = Instant.now(), lender = LENDER2.party, amount = 200.POUNDS)
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.ADD_RESTRICTION, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE, charges = setOf(charge), buyerConveyancer = CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(CHARLIE.publicKey), AddConsentForNewChargeTests.DummyCommand())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Input State`() {
        val charge = Charge(date = Instant.now(), lender = LENDER2.party, amount = 200.POUNDS)
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.ADD_RESTRICTION, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE, charges = setOf(charge), buyerConveyancer = CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Output State`() {
        val charge = Charge(date = Instant.now(), lender = LENDER2.party, amount = 200.POUNDS)
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.ADD_RESTRICTION, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE, charges = setOf(charge), buyerConveyancer = CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By The Buyer Conveyancer`() {
        val charge = Charge(date = Instant.now(), lender = LENDER2.party, amount = 200.POUNDS)
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.ADD_RESTRICTION, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE, charges = setOf(charge), buyerConveyancer = CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(BOB.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.verifies()
            }
        }
    }

    @Test
    fun `status Of Input State Must Be ASSIGN_BUYER_CONVEYANCER`() {
        val charge = Charge(date = Instant.now(), lender = LENDER2.party, amount = 200.POUNDS)
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.ADD_RESTRICTION, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE, charges = setOf(charge), buyerConveyancer = CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.CONSENT_FOR_DISCHARGE,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.verifies()
            }
        }
    }

    @Test
    fun `status Of Output State Must Be CONSENT_FOR_NEW_CHARGE`() {
        val charge = Charge(date = Instant.now(), lender = LENDER2.party, amount = 200.POUNDS)
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.ADD_RESTRICTION, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE, charges = setOf(charge), buyerConveyancer = CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(status = DTCConsentStatus.ISSUED))
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.verifies()
            }
        }
    }

    @Test
    fun `new Restrictions Must Be Added With Consents`() {
        val charge = Charge(date = Instant.now(), lender = LENDER2.party, amount = 200.POUNDS)
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.ADD_RESTRICTION, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE, charges = setOf(charge), buyerConveyancer = CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.ADD_RESTRICTION, false, charge)
                val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE, charges = setOf(charge), buyerConveyancer = CHARLIE.party)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(status = DTCConsentStatus.ISSUED))
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.verifies()
            }
        }
    }

    @Test
    fun `new Restrictions Added Must Have Action ADD_RESTRICTION`() {
        val charge = Charge(date = Instant.now(), lender = LENDER2.party, amount = 200.POUNDS)
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.ADD_RESTRICTION, true, charge)
        val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE, charges = setOf(charge), buyerConveyancer = CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                val chargeRestriction = ChargeRestriction("CBCR", restrictionText, LENDER1.party, ActionOnRestriction.NO_ACTION, true, charge)
                val outputProposedChargeAndRestrictionState = proposedChargeOrRestrictionState.copy(restrictions = setOf(chargeRestriction), status = DTCConsentStatus.CONSENT_FOR_NEW_CHARGE, charges = setOf(charge), buyerConveyancer = CHARLIE.party)
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState.copy(status = DTCConsentStatus.ISSUED))
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.fails()
            }
            transaction {
                input(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, proposedChargeOrRestrictionState.copy(status = DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER,  buyerConveyancer = CHARLIE.party))
                output(ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID, outputProposedChargeAndRestrictionState)
                command(listOf(CHARLIE.publicKey), ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge())
                this.verifies()
            }
        }
    }
}