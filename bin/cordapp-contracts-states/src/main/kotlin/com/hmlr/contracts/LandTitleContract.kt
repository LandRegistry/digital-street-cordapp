package com.hmlr.contracts

import com.hmlr.model.ActionOnRestriction
import com.hmlr.model.ChargeRestriction
import com.hmlr.model.LandTitleStatus
import com.hmlr.states.LandAgreementState
import com.hmlr.states.LandTitleState
import com.hmlr.states.ProposedChargesAndRestrictionsState
import com.hmlr.states.RequestIssuanceState
import com.hmlr.utils.checkPropertyInvariants
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

/**
 * Contract code to govern the [LandTitleState]
 * [Commands.IssueLandTitle] Command logic verifies land title issuance transaction
 * [Commands.AssignBuyerConveyancer] Command logic verifies buyer's conveyancer assignment to [LandAgreementState] during Draft Agreement transaction
 * [Commands.TransferLandTitle] Command logic verifies land title transfer transaction
 */
class LandTitleContract: Contract {

    /**
     * Define Contract ID
     */
    companion object {
        @JvmStatic
        val LAND_TITLE_CONTRACT_ID = LandTitleContract::class.java.name!!
    }

    /**
     * List of all commands classes supported by the [LandTitleContract]
     */
    interface Commands: CommandData{
        class IssueLandTitle: TypeOnlyCommandData(), Commands
        class AssignBuyerConveyancer: TypeOnlyCommandData(), Commands
        class TransferLandTitle: TypeOnlyCommandData(), Commands
    }


    /**
     * Pure [Boolean] function to verify the incoming transaction
     * @param tx [LedgerTransaction]
     * @return [Boolean]
     */
    override fun verify(tx: LedgerTransaction) {
        val commands = tx.commands.requireSingleCommand<Commands>()
        val setOfSigners = commands.signers.toSet()
        when (commands.value){
            is Commands.IssueLandTitle -> verifyIssueLandTitle(tx, setOfSigners)
            is Commands.AssignBuyerConveyancer -> verifyAssignBuyerConveyancer(tx, setOfSigners)
            is Commands.TransferLandTitle -> verifyTransferLandTitle(tx, setOfSigners)

            else -> throw IllegalArgumentException("Unrecognised Command")
        }
    }

    /**
     * validation logic for [Commands.IssueLandTitle] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyIssueLandTitle(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "One input should be consumed while issuing Land Title Asset on the ledger" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<RequestIssuanceState>().single()
        val outputProposedChargeAndRestrictionState = tx.outputsOfType<ProposedChargesAndRestrictionsState>().single()

        "There should be exactly three output states" using(tx.outputs.size == 3)
        val output = tx.outputsOfType<LandTitleState>().single()
        "Issuing party and Delegating party must be different" using (output.titleIssuer != output.landTitleProperties.ownerConveyancer)
        "Seller's identity must have been verified" using(output.landTitleProperties.owner.confirmationOfIdentity)
        "Issuance transaction must be signed by the Issuer" using(setOfSigners.contains(output.titleIssuer.owningKey))
        "Owner's conveyancer, Owner's lender and the Issuer must be in the list of participants" using(output.participants.containsAll(listOf(output.titleIssuer, output.landTitleProperties.ownerConveyancer, output.landTitleProperties.ownerLender)))
        "Status should be set to `ISSUED`" using (output.status == LandTitleStatus.ISSUED)
        "Issued Land Title should not have any offer price" using(output.lastSoldValue == null)
        "Issued land title number should be equal to the requested title number" using(output.titleID == input.titleID)
        "Owner in the consumed request issuance state and owner in the land title state must be same" using(output.landTitleProperties.owner.equals(input.seller))
        "Seller Conveyancer in the consumed request issuance state and owner conveyancer in the land title state must be same" using(output.landTitleProperties.ownerConveyancer == input.sellerConveyancer)
        "Linear id of the proposed charge and restriction state in the title state must be equal to the linear id of the newly issued state" using(outputProposedChargeAndRestrictionState.linearId.toString() == output.proposedChargeOrRestrictionLinearId)
    }

    /**
     * validation logic for [Commands.AssignBuyerConveyancer] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyAssignBuyerConveyancer(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {
        "There must be exactly two input state" using(tx.inputs.size == 2)
        val inputLandTitleState = tx.inputsOfType<LandTitleState>().single()

        "There must be exactly three output states" using(tx.outputs.size == 3)
        val outputLandTitleState = tx.outputsOfType<LandTitleState>().single()
        val outputAgreementState = tx.outputsOfType<LandAgreementState>().single()
        "Status of consumed Land Title state must not be be 'ASSIGN_BUYER_CONVEYANCER'" using(inputLandTitleState.status != LandTitleStatus.ASSIGN_BUYER_CONVEYANCER)
        "Status of Land title state in the output must be 'ASSIGN_BUYER_CONVEYANCER'" using(outputLandTitleState.status == LandTitleStatus.ASSIGN_BUYER_CONVEYANCER)
        "Buyer's conveyancer must be added to the participants list" using(outputLandTitleState.participants.containsAll(inputLandTitleState.participants + outputAgreementState.buyerConveyancer))
        "Mismatch in the output and input Land Title state" using(inputLandTitleState == outputLandTitleState.copy(participants = inputLandTitleState.participants, status = inputLandTitleState.status, charges = inputLandTitleState.charges, restrictions = inputLandTitleState.restrictions) && inputLandTitleState.restrictions.toSortedSet().equals(outputLandTitleState.restrictions.toSortedSet()) && inputLandTitleState.charges.toSortedSet().equals(outputLandTitleState.charges.toSortedSet()))
        "Transaction must be signed by the owner's conveyancer" using(setOfSigners.containsAll(listOf(inputLandTitleState.landTitleProperties.ownerConveyancer.owningKey)))
    }

    /**
     * validation logic for [Commands.TransferLandTitle] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyTransferLandTitle(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {
        "Three input states should be consumed while transferring land title" using (tx.inputs.size == 3)
        val inputLandTitleState = tx.inputsOfType<LandTitleState>().single()
        val inputAgreementState = tx.inputsOfType<LandAgreementState>().single()
        val inputProposedChargeAndRestrictionState = tx.inputsOfType<ProposedChargesAndRestrictionsState>().single()

        "Three output states should be produced while transferring land title" using (tx.outputs.size == 3)
        val outputLandTitleState = tx.outputsOfType<LandTitleState>().single()

        "Status of consumed land title state must be 'ASSIGN_BUYER_CONVEYANCER'" using (inputLandTitleState.status == LandTitleStatus.ASSIGN_BUYER_CONVEYANCER)
        "Status of produced land title state must be 'TRANSFERRED'" using (outputLandTitleState.status == LandTitleStatus.TRANSFERRED)
        "New owner must be set to the buyer specified in the agreement state" using (outputLandTitleState.landTitleProperties.owner == inputAgreementState.buyer.copy(signature = outputLandTitleState.landTitleProperties.owner.signature))
        "New owner's conveyancer must be set to the conveyancer specified in agreement state" using (outputLandTitleState.landTitleProperties.ownerConveyancer == inputAgreementState.buyerConveyancer)
        "Last sold value of the land title state must be updated in the output state" using (outputLandTitleState.lastSoldValue == inputAgreementState.purchasePrice)
        "Both the buyer's and seller's conveyancer must sign the transaction" using (setOfSigners.containsAll(listOf(inputLandTitleState.landTitleProperties.ownerConveyancer.owningKey, inputAgreementState.buyerConveyancer.owningKey, inputLandTitleState.titleIssuer.owningKey)))
        "Restrictions in the title state must be equal to the restrictions present in proposed charge and restrictions state" using (outputLandTitleState.charges.toSortedSet().equals(inputProposedChargeAndRestrictionState.charges.toSortedSet()) && outputLandTitleState.restrictions.toSortedSet().equals(inputProposedChargeAndRestrictionState.restrictions.toSortedSet()))
        "Land title state must contain new charges" using (outputLandTitleState.charges.containsAll(inputProposedChargeAndRestrictionState.charges))
        "Old charges should not be removed from the title state" using (outputLandTitleState.charges.containsAll(inputLandTitleState.charges))
        val invariantProperties = setOf(
                LandTitleState::titleID,
                LandTitleState::linearId,
                LandTitleState::titleIssuer,
                LandTitleState::titleType,
                LandTitleState::proposedChargeOrRestrictionLinearId
        )
        "Only title issuer, ownerConveyancer and ownerLender must be participant to the title state" using(outputLandTitleState.participants.containsAll(listOf(inputLandTitleState.titleIssuer, inputAgreementState.buyerConveyancer, outputLandTitleState.landTitleProperties.ownerLender)) && outputLandTitleState.participants.size == 3)
        "Mismatch in the output and input Land Title state" using (checkPropertyInvariants(inputLandTitleState, outputLandTitleState, invariantProperties))
        outputLandTitleState.restrictions.forEach {
            require(it.action == ActionOnRestriction.NO_ACTION) { "Restrictions should have no actions when issued on ledger" }
            require(!it.consentGiven) { "Consent given should be false on Restrictions when issued on ledger" }
            when (it) {
                is ChargeRestriction -> {
                    require(it.consentingParty == outputLandTitleState.landTitleProperties.ownerLender) { "Consenting Party for the restrictions must be equal to owner Lender in title state" }
                }
            }
        }
    }
}