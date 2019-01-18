package com.hmlr.contracts

import com.hmlr.model.LandTitleStatus
import com.hmlr.states.LandAgreementState
import com.hmlr.states.LandTitleState
import com.hmlr.states.RequestIssuanceState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

/**
 * Land Title Contract class with rules/business logic definition which governs the evolution of [LandTitleState]
 *
 **/
class LandTitleContract: Contract {

    companion object {
        @JvmStatic
        val LAND_TITLE_CONTRACT_ID = LandTitleContract::class.java.name!!
    }

    interface Commands: CommandData{
        class IssueLandTitle: TypeOnlyCommandData(), Commands
        class TransferLandTitle: TypeOnlyCommandData(), Commands
        class AssignBuyerConveyancer: TypeOnlyCommandData(), Commands
    }

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

    private fun verifyIssueLandTitle(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "One input should be consumed while issuing Land Title Asset on the ledger" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<RequestIssuanceState>().single()

        "There should be exactly two output states" using(tx.outputs.size == 2)
        val output = tx.outputsOfType<LandTitleState>().single()
        "Issuing party and Delegating party must be different" using (output.titleIssuer != output.landTitleProperties.ownerConveyancer)
        "Seller's identity must have been verified" using(output.landTitleProperties.owner!!.confirmationOfIdentity)
        "Issuance transaction must be signed by the Issuer" using(setOfSigners.contains(output.titleIssuer.owningKey))
        "Delegated party and Issuer must be in the list of participants" using(output.participants.containsAll(listOf(output.titleIssuer, output.landTitleProperties.ownerConveyancer!!)))
        "Status should be set to `ISSUED`" using (output.status == LandTitleStatus.ISSUED)
        "Issued Land Title should not have any offer price" using(output.lastSoldValue == null)
        "Issued land title number should be equal to the requested title number" using(output.titleID == input.titleID)
    }

    private fun verifyAssignBuyerConveyancer(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {
        "There must be exactly one input state" using(tx.inputs.size == 1)
        val inputLandTitleState = tx.inputsOfType<LandTitleState>().single()

        "There must be exactly two output states" using(tx.outputs.size == 2)
        val outputLandTitleState = tx.outputsOfType<LandTitleState>().single()
        val outputAgreementState = tx.outputsOfType<LandAgreementState>().single()

        "Buyer's conveyancer must be added to the participants list" using(outputLandTitleState.participants.containsAll(inputLandTitleState.participants + outputAgreementState.buyerConveyancer))
        "Mismatch in the output and input Land Title state" using(inputLandTitleState == outputLandTitleState.copy(participants = inputLandTitleState.participants, status = inputLandTitleState.status))
        "Transaction must be signed by the owner's conveyancer" using(setOfSigners.containsAll(listOf(inputLandTitleState.landTitleProperties.ownerConveyancer!!.owningKey)))
    }

    // To Be completed a week after sprint 11. Just commenting for now to keep track of TODO things
    private fun verifyTransferLandTitle(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {
        /*"Two input states should be consumed while transferring land title" using(tx.inputs.size == 2)
        val inputLandTitleState = tx.inputsOfType<LandTitleState>().single()
        val inputAgreementState = tx.inputsOfType<LandAgreementState>().single()

        "Two output states should be produced while transferring land title" using(tx.outputs.size == 2)
        val outputLandTitleState = tx.outputsOfType<LandTitleState>().single()

        "Status of consumed land title state must be 'ASSIGN_BUYER_CONVEYANCER'" using(inputLandTitleState.status == LandTitleStatus.ASSIGN_BUYER_CONVEYANCER)
        "Status of produced land title state must be 'TRANSFERRED'" using(outputLandTitleState.status == LandTitleStatus.TRANSFERRED)
        "New owner must be set to the buyer specified in the agreement state" using (outputLandTitleState.landTitleProperties.owner == inputAgreementState.buyer)
        "New owner's conveyancer must be set to the conveyancer specified in agreement state" using(outputLandTitleState.landTitleProperties.ownerConveyancer == inputAgreementState.buyerConveyancer)
        "Last sold value of the land title state must be updated in the output state" using(outputLandTitleState.lastSoldValue == inputAgreementState.purchasePrice)
        "Both the buyer's and seller's conveyancer must sign the transaction" using(setOfSigners.containsAll(listOf(inputLandTitleState.landTitleProperties.ownerConveyancer!!.owningKey, inputAgreementState.buyerConveyancer.owningKey)))
        "Mismatch in the output and input Land Title state" using(inputLandTitleState == outputLandTitleState.copy(status = inputLandTitleState.status, lastSoldValue = inputLandTitleState.lastSoldValue, landTitleProperties = outputLandTitleState.landTitleProperties.copy(ownerConveyancer = inputLandTitleState.landTitleProperties.ownerConveyancer, owner = inputLandTitleState.landTitleProperties.owner)))
  */  }
}