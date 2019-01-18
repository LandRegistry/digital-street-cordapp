package com.hmlr.contracts

import com.hmlr.model.*
import com.hmlr.states.LandAgreementState
import com.hmlr.states.LandTitleState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey
import com.hmlr.states.ProposedChargesAndRestrictionsState
import com.hmlr.utils.checkPropertyInvariants

/**
 * Contract code to govern the [ProposedChargesAndRestrictionsState]
 * [Commands.IssueProposedChargeAndRestriction] Command logic verifies issue charge and restriction
 * [Commands.RequestForDischarge] Command logic verifies to request for a discharge
 * [Commands.AddConsentForDischarge] Command logic verifies consent to give consent for a discharge
 * [Commands.AssignBuyerConveyancer] Command logic verifies consent to give consent for a discharge
 * [Commands.AddConsentForNewCharge] Command logic verifies consent to add a new charge and give consent for it
 */
class ProposedChargeAndRestrictionContract: Contract {

    /**
     * Define Contract ID
     */
    companion object {
        @JvmStatic
        val PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID = ProposedChargeAndRestrictionContract::class.java.name!!
    }

    /**
     * List of all commands classes supported by the [ProposedChargeAndRestrictionContract]
     */
    interface Commands : CommandData {
        class IssueProposedChargeAndRestriction : TypeOnlyCommandData(), ProposedChargeAndRestrictionContract.Commands
        class RequestForDischarge : TypeOnlyCommandData(), ProposedChargeAndRestrictionContract.Commands
        class AddConsentForDischarge : TypeOnlyCommandData(), ProposedChargeAndRestrictionContract.Commands
        class AssignBuyerConveyancer : TypeOnlyCommandData(), ProposedChargeAndRestrictionContract.Commands
        class AddConsentForNewCharge : TypeOnlyCommandData(), ProposedChargeAndRestrictionContract.Commands
        class TransferLandTitle : TypeOnlyCommandData(), ProposedChargeAndRestrictionContract.Commands
    }

    /**
     * Pure [Boolean] function to verify the incoming transaction
     * @param tx [LedgerTransaction]
     * @return [Boolean]
     */
    override fun verify(tx: LedgerTransaction) {
        val commands = tx.commands.requireSingleCommand<ProposedChargeAndRestrictionContract.Commands>()
        val setOfSigners = commands.signers.toSet()
        when (commands.value) {
            is ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction -> verifyIssueProposedChargeAndRestriction(tx, setOfSigners)
            is ProposedChargeAndRestrictionContract.Commands.RequestForDischarge -> verifyRequestForDischarge(tx, setOfSigners)
            is ProposedChargeAndRestrictionContract.Commands.AddConsentForDischarge -> verifyAddConsentForDischarge(tx, setOfSigners)
            is ProposedChargeAndRestrictionContract.Commands.AddConsentForNewCharge -> verifyAddConsentForNewCharge(tx, setOfSigners)
            is ProposedChargeAndRestrictionContract.Commands.AssignBuyerConveyancer -> verifyAssignBuyerConveyancer(tx, setOfSigners)
            is ProposedChargeAndRestrictionContract.Commands.TransferLandTitle -> verifyTransferLandTitle(tx, setOfSigners)
            else -> throw IllegalArgumentException("Unrecognised Command")
        }
    }

    /**
     * validation logic for [Commands.IssueProposedChargeAndRestriction] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyIssueProposedChargeAndRestriction(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {

        "Only one input state should be consumed during creation of proposed charge and restriction state" using (tx.inputs.size == 1)
        val titleState = tx.outRefsOfType<LandTitleState>().single()
        val proposedChargeAndRestrictionState = tx.outRefsOfType<ProposedChargesAndRestrictionsState>().single()
        "Exactly three output states should be produced during creation of proposed charge and restriction state" using (tx.outputs.size == 3)
        "Only land title issuer can issue proposed charge and restriction state" using (setOfSigners.size == 1 && setOfSigners.contains(titleState.state.data.titleIssuer.owningKey))
        "Title id in the proposed charge and restriction sate must match with the title id in the land title state" using (proposedChargeAndRestrictionState.state.data.titleID == titleState.state.data.titleID)
        "List of charges in the proposed charges and restriction state and title state must be equal" using (proposedChargeAndRestrictionState.state.data.charges.toSortedSet().equals(titleState.state.data.charges.toSortedSet()))
        "List of restrictions in the proposed charges and restriction state and title state must be equal" using (proposedChargeAndRestrictionState.state.data.restrictions.toSortedSet().equals(titleState.state.data.restrictions.toSortedSet()))
        "No consents must be provided during issuance of proposed charge and restriction state" using (proposedChargeAndRestrictionState.state.data.addNewChargeConsented == false && proposedChargeAndRestrictionState.state.data.dischargeConsented == false)
        proposedChargeAndRestrictionState.state.data.restrictions.forEach {
            require(it.action == ActionOnRestriction.NO_ACTION) { "Restrictions should have no actions when issued on ledger" }
            require(!it.consentGiven) { "Consent given should be false on Restrictions when issued on ledger" }
            when (it) {
                is ChargeRestriction -> {
                    require(it.consentingParty == titleState.state.data.landTitleProperties.ownerLender) { "Consenting Party for the restrictions must be equal to owner Lender in title state" }
                }
            }
            "Owner conveyancer must match in the title state and proposed charges and restriction state" using (titleState.state.data.landTitleProperties.ownerConveyancer == proposedChargeAndRestrictionState.state.data.ownerConveyancer)
            "Buyer's conveyancer must be null" using (proposedChargeAndRestrictionState.state.data.buyerConveyancer == null)
            "Participants list in the proposed charge and restriction state must include title issuer, owner's conveyancer and owner's lender" using (proposedChargeAndRestrictionState.state.data.participants.containsAll(titleState.state.data.participants) && proposedChargeAndRestrictionState.state.data.participants.size == titleState.state.data.participants.size)
            "Status of proposed charge and restriction state must be ISSUED" using (proposedChargeAndRestrictionState.state.data.status == DTCConsentStatus.ISSUED)
        }
    }

    /**
     * validation logic for [Commands.RequestForDischarge] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyRequestForDischarge(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {

        "Only one input should be consumed when requesting for discharge" using (tx.inputStates.size == 1)
        val inputProposedChargesAndRestrictionsState = tx.inputsOfType<ProposedChargesAndRestrictionsState>().single()

        "Only one output should be produced when requesting for discharge" using (tx.outputStates.size == 1)
        val outputProposedChargesAndRestrictionsState = tx.outputsOfType<ProposedChargesAndRestrictionsState>().single()
        "Ony owner conveyancer can sign the transaction" using (setOfSigners.size == 1 && setOfSigners.contains(inputProposedChargesAndRestrictionsState.ownerConveyancer.owningKey))
        "Status of input must be ISSUED" using (inputProposedChargesAndRestrictionsState.status == DTCConsentStatus.ISSUED)
        "Status of output must be REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE" using (outputProposedChargesAndRestrictionsState.status == DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
        val invariantProperties = setOf(
                ProposedChargesAndRestrictionsState::titleID,
                ProposedChargesAndRestrictionsState::linearId,
                ProposedChargesAndRestrictionsState::participants,
                ProposedChargesAndRestrictionsState::ownerConveyancer,
                ProposedChargesAndRestrictionsState::buyerConveyancer,
                ProposedChargesAndRestrictionsState::charges,
                ProposedChargesAndRestrictionsState::addNewChargeConsented,
                ProposedChargesAndRestrictionsState::dischargeConsented
        )
        "Mismatch in the input and output states of proposed charge and restriction state" using (checkPropertyInvariants(inputProposedChargesAndRestrictionsState, outputProposedChargesAndRestrictionsState, invariantProperties))
        val invariantRestrictionProperties = setOf(
                Restriction::restrictionId,
                Restriction::consentingParty,
                Restriction::restrictionText,
                Restriction::consentGiven
        )
        "Restrictions list size should remain the same" using (inputProposedChargesAndRestrictionsState.restrictions.size == outputProposedChargesAndRestrictionsState.restrictions.size)
        val pairOfRestrictions = inputProposedChargesAndRestrictionsState.restrictions.toSortedSet() zip outputProposedChargesAndRestrictionsState.restrictions.toSortedSet()
        pairOfRestrictions.map {
            require(checkPropertyInvariants(it.first, it.second, invariantRestrictionProperties)) { "Only ActionOnRestriction field is allowed to be changed in the Restrictions list" }
            when(it.first) {
                is ChargeRestriction -> {
                    require(checkPropertyInvariants(it.first as ChargeRestriction, it.second as ChargeRestriction, setOf(ChargeRestriction::charge))) {"Charge data on the ChargeRestriction should not be changed"}
                }
            }
        }
        outputProposedChargesAndRestrictionsState.restrictions.forEach {
            when (it) {
                is ChargeRestriction -> {
                    require(it.action == ActionOnRestriction.DISCHARGE) { "Action must be DISCHARGE in the updated restrictions list" }
                }
            }
        }
    }

    /**
     * validation logic for [Commands.AddConsentForDischarge] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyAddConsentForDischarge(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {

        "Only one input should be consumed when requesting for discharge" using (tx.inputStates.size == 1)
        val inputProposedChargesAndRestrictionsState = tx.inputsOfType<ProposedChargesAndRestrictionsState>().single()

        "Only one output should be produced when requesting for discharge" using (tx.outputStates.size == 1)
        val outputProposedChargesAndRestrictionsState = tx.outputsOfType<ProposedChargesAndRestrictionsState>().single()
        "There can be only one signer for the transaction" using(setOfSigners.size == 1)
        "Restrictions list size should remain the same" using (inputProposedChargesAndRestrictionsState.restrictions.size == outputProposedChargesAndRestrictionsState.restrictions.size)
        val pairOfRestrictions = inputProposedChargesAndRestrictionsState.restrictions.toSortedSet() zip outputProposedChargesAndRestrictionsState.restrictions.toSortedSet()
        val invariantRestrictionProperties = setOf(
                Restriction::restrictionId,
                Restriction::consentingParty,
                Restriction::restrictionText,
                Restriction::action
        )
        pairOfRestrictions.map {
            require(checkPropertyInvariants(it.first, it.second, invariantRestrictionProperties)) { "Only ConsentGiven field is allowed to be changed in the Restrictions list" }
            when(it.first) {
                is ChargeRestriction -> {
                    require(checkPropertyInvariants(it.first as ChargeRestriction, it.second as ChargeRestriction, setOf(ChargeRestriction::charge))) {"Charge data on the ChargeRestriction should not be changed"}
                }
            }
        }
        outputProposedChargesAndRestrictionsState.restrictions.forEach {
            when(it) {
                is ChargeRestriction -> {
                    require(setOfSigners.contains(it.consentingParty.owningKey)) {"Only the consenting party can give consent for the restrictions"}
                    require (it.consentGiven) {"Consent given flag should be set to true"}
                }
            }
        }
        val invariantProperties = setOf(
                ProposedChargesAndRestrictionsState::titleID,
                ProposedChargesAndRestrictionsState::linearId,
                ProposedChargesAndRestrictionsState::participants,
                ProposedChargesAndRestrictionsState::ownerConveyancer,
                ProposedChargesAndRestrictionsState::buyerConveyancer,
                ProposedChargesAndRestrictionsState::charges,
                ProposedChargesAndRestrictionsState::addNewChargeConsented,
                ProposedChargesAndRestrictionsState::dischargeConsented
        )
        "Mismatch in the input and output states of proposed charge and restriction state" using (checkPropertyInvariants(inputProposedChargesAndRestrictionsState, outputProposedChargesAndRestrictionsState, invariantProperties))
        "Status of input must be REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE" using (inputProposedChargesAndRestrictionsState.status == DTCConsentStatus.REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE)
        "Status of output must be CONSENT_FOR_DISCHARGE" using (outputProposedChargesAndRestrictionsState.status == DTCConsentStatus.CONSENT_FOR_DISCHARGE)
    }

    private fun verifyAssignBuyerConveyancer(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {
        "There must be exactly two input states" using(tx.inputs.size == 2)
        val outputLandTitleAgreementState = tx.outputsOfType<LandAgreementState>().single()

        val inputProposedChargesAndRestrictionsState = tx.inputsOfType<ProposedChargesAndRestrictionsState>().single()
        "There must be exactly three output states" using(tx.outputs.size == 3)

        val outputProposedChargesAndRestrictionsState = tx.outputsOfType<ProposedChargesAndRestrictionsState>().single()
        val invariantProperties = setOf(
                ProposedChargesAndRestrictionsState::titleID,
                ProposedChargesAndRestrictionsState::linearId,
                ProposedChargesAndRestrictionsState::ownerConveyancer,
                ProposedChargesAndRestrictionsState::addNewChargeConsented,
                ProposedChargesAndRestrictionsState::dischargeConsented
        )
        "Status of output proposed charge and restriction state should be ASSIGN_BUYER_CONVEYANCER" using(outputProposedChargesAndRestrictionsState.status == DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER)
        "Owner conveyancer should sign the transaction" using(setOfSigners.size == 1 && setOfSigners.contains(outputLandTitleAgreementState.sellerConveyancer.owningKey))
        "Mismatch in the input and output states of proposed charge and restriction state" using (checkPropertyInvariants(inputProposedChargesAndRestrictionsState, outputProposedChargesAndRestrictionsState, invariantProperties) && inputProposedChargesAndRestrictionsState.restrictions.toSortedSet().equals(outputProposedChargesAndRestrictionsState.restrictions.toSortedSet()) && inputProposedChargesAndRestrictionsState.charges.toSortedSet().equals(outputProposedChargesAndRestrictionsState.charges.toSortedSet()))
        "Proposed charge and restriction state must be sent to the correct conveyancer for adding new charge" using(outputProposedChargesAndRestrictionsState.buyerConveyancer == outputLandTitleAgreementState.buyerConveyancer)
        "Only Buyer conveyancer must be added to the participants list" using(outputProposedChargesAndRestrictionsState.participants.containsAll(inputProposedChargesAndRestrictionsState.participants + outputLandTitleAgreementState.buyerConveyancer) && outputProposedChargesAndRestrictionsState.participants.size == inputProposedChargesAndRestrictionsState.participants.size + 1)
    }

    /**
     * validation logic for [Commands.AddConsentForNewCharge] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyAddConsentForNewCharge(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {
        "Only one input should be consumed when requesting for discharge" using (tx.inputStates.size == 1)
        val inputProposedChargesAndRestrictionsState = tx.inputsOfType<ProposedChargesAndRestrictionsState>().single()
        "Only one output should be produced when requesting for discharge" using (tx.outputStates.size == 1)
        val outputProposedChargesAndRestrictionsState = tx.outputsOfType<ProposedChargesAndRestrictionsState>().single()
        "Buyer conveyancer must not be null" using(inputProposedChargesAndRestrictionsState.buyerConveyancer != null)
        "Only buyer conveyancer can sign the transaction" using(setOfSigners.size == 1 && setOfSigners.contains(inputProposedChargesAndRestrictionsState.buyerConveyancer!!.owningKey))
        outputProposedChargesAndRestrictionsState.restrictions.forEach {
            require(it.action == ActionOnRestriction.ADD_RESTRICTION) { "New Restrictions should have action as ADD_RESTRICTION" }
            require(it.consentGiven) { "Consent given should be true on new Restrictions" }
        }

        val invariantProperties = setOf(
                ProposedChargesAndRestrictionsState::titleID,
                ProposedChargesAndRestrictionsState::linearId,
                ProposedChargesAndRestrictionsState::participants,
                ProposedChargesAndRestrictionsState::ownerConveyancer,
                ProposedChargesAndRestrictionsState::buyerConveyancer,
                ProposedChargesAndRestrictionsState::addNewChargeConsented,
                ProposedChargesAndRestrictionsState::dischargeConsented
        )
        "Mismatch in the input and output states of proposed charge and restriction state" using (checkPropertyInvariants(inputProposedChargesAndRestrictionsState, outputProposedChargesAndRestrictionsState, invariantProperties))
        "Status of input must be ASSIGN_BUYER_CONVEYANCER" using (inputProposedChargesAndRestrictionsState.status == DTCConsentStatus.ASSIGN_BUYER_CONVEYANCER)
        "Status of output must be CONSENT_FOR_NEW_CHARGE" using (outputProposedChargesAndRestrictionsState.status == DTCConsentStatus.CONSENT_FOR_NEW_CHARGE)
    }

    private fun verifyTransferLandTitle(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {
        "Three input states should be consumed while transferring land title" using (tx.inputs.size == 3)
        val inputProposedChargeAndRestrictionState = tx.inputsOfType<ProposedChargesAndRestrictionsState>().single()

        "Three output states should be produced while transferring land title" using (tx.outputs.size == 3)
        val outputProposedChargesAndRestrictionsState = tx.outputsOfType<ProposedChargesAndRestrictionsState>().single()
        val outputLandTitleState = tx.outputsOfType<LandTitleState>().single()
        val inputLandAgreementState = tx.inputsOfType<LandAgreementState>().single()
        "Status of consumed proposed charge and restriction state should be CONSENT_FOR_NEW_CHARGE" using (inputProposedChargeAndRestrictionState.status == DTCConsentStatus.CONSENT_FOR_NEW_CHARGE)
        "Buyer conveyancer must be null in the output" using (outputProposedChargesAndRestrictionsState.buyerConveyancer == null)
        "Owner conveyancer in the output should be same as buyer conveyancer in the input" using (outputProposedChargesAndRestrictionsState.ownerConveyancer == inputProposedChargeAndRestrictionState.buyerConveyancer)
        "Status of output proposed charge and restriction state should be ISSUED" using (outputProposedChargesAndRestrictionsState.status == DTCConsentStatus.ISSUED)
        "Only title issuer, ownerConveyancer and ownerLender must be participant to the title state" using(outputLandTitleState.participants.containsAll(listOf(outputLandTitleState.titleIssuer, outputLandTitleState.landTitleProperties.ownerConveyancer, outputLandTitleState.landTitleProperties.ownerLender)) && outputLandTitleState.participants.size == 3)
        "Transaction should be signed by both the conveyancer and title issuer" using(setOfSigners.containsAll(setOf(inputLandAgreementState.buyerConveyancer.owningKey, inputLandAgreementState.sellerConveyancer.owningKey, outputLandTitleState.titleIssuer.owningKey)))
        outputProposedChargesAndRestrictionsState.restrictions.forEach {
            require(it.action == ActionOnRestriction.NO_ACTION) { "Restrictions should have no actions when issued on ledger" }
            require(!it.consentGiven) { "Consent given should be false on Restrictions when issued on ledger" }
            when (it) {
                is ChargeRestriction -> {
                    require(it.consentingParty == outputLandTitleState.landTitleProperties.ownerLender) { "Consenting Party for the restrictions must be equal to owner Lender in title state" }
                }
            }
        }
        val invariantProperties = setOf(
                ProposedChargesAndRestrictionsState::titleID,
                ProposedChargesAndRestrictionsState::linearId,
                ProposedChargesAndRestrictionsState::addNewChargeConsented,
                ProposedChargesAndRestrictionsState::dischargeConsented
        )
        "Mismatch in the input and output states of proposed charge and restriction state" using (checkPropertyInvariants(inputProposedChargeAndRestrictionState, outputProposedChargesAndRestrictionsState, invariantProperties))
    }
}