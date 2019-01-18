package com.hmlr.contracts

import com.hmlr.model.Status
import com.hmlr.states.LandTitleState
import com.hmlr.states.RequestIssuanceState
import net.corda.core.contracts.*
import net.corda.core.crypto.Crypto
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
        class LandTitleTransferRequest: TypeOnlyCommandData(), Commands
        class LandTitleTransferResponse: TypeOnlyCommandData(), Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val commands = tx.commands.requireSingleCommand<Commands>()
        val setOfSigners = commands.signers.toSet()
        when (commands.value){
            is Commands.IssueLandTitle -> verifyIssueLandTitle(tx, setOfSigners)
            is Commands.LandTitleTransferRequest -> verifyLandTitleTransferRequest(tx)
            is Commands.LandTitleTransferResponse -> verifyLandTitleTransferResponse(tx)
            else -> throw IllegalArgumentException("Unrecognised Command")
        }
    }

    private fun verifyIssueLandTitle(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "One input should be consumed while issuing Land Title Asset on the ledger" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<RequestIssuanceState>().single()

        "There should be exactly one output state" using(tx.outputs.size == 2)
        val output = tx.outputsOfType<LandTitleState>().single()
        "Issuing party and Delegating party must be different" using (output.titleIssuer != output.landTitleProperties.ownerConveyancer)
        "Seller's identity must have been verified" using(output.landTitleProperties.owner!!.confirmationOfIdentity)
        "Issuance transaction must be signed by the Issuer" using(setOfSigners.contains(output.titleIssuer.owningKey))
        "Delegated party and Issuer must be in the list of participants" using(output.participants.containsAll(listOf(output.titleIssuer, output.landTitleProperties.ownerConveyancer!!)))
        "Status should be set to `ISSUED`" using (output.status == Status.ISSUED)
        "Issued Land Title should not have any offer price" using(output.lastSoldValue == null)
        "Issued land title number should be equal to the requested title number" using(output.titleID == input.titleID)
    }

    private fun verifyLandTitleTransferRequest(tx: LedgerTransaction) = requireThat{
        val input = tx.inputsOfType<LandTitleState>().single()
        val output = tx.outputsOfType<LandTitleState>().single()

        "There should be exactly one input state" using(tx.inputs.size == 1)
        "There should be exactly one output state" using(tx.outputs.size == 1)
        "Buyer and Seller must be different" using (output.landTitleProperties.buyer != output.landTitleProperties.owner)
        "Accepting Party (Buyer's Conveyancer) should be added to the participants list" using(output.participants.containsAll(input.participants + output.landTitleProperties.buyerConveyancer))
        val verify = Crypto.doVerify(input.landTitleProperties.owner!!.publicKey!!, output.landTitleProperties.owner!!.signature!!, input.titleID.toByteArray())
        "Only Land Title owner can initiate the Land Transfer Request" using(verify == true)
        "Status of Consumed LandTitle state must not be `PENDING_BUYER_APPROVAL`" using (input.status != Status.PENDING_BUYER_APPROVAL)
        "Status of Updated LandTitle state must be `PENDING_BUYER_APPROVAL`" using(output.status == Status.PENDING_BUYER_APPROVAL)
        "Mismatch in the input and output states" using(input == output.copy(status = input.status, participants = input.participants, landTitleProperties = output.landTitleProperties.copy(buyer = input.landTitleProperties.buyer, buyerConveyancer = input.landTitleProperties.buyerConveyancer, owner = output.landTitleProperties.owner.copy(signature = input.landTitleProperties.owner.signature))))
    }

    private fun verifyLandTitleTransferResponse(tx: LedgerTransaction) = requireThat{
        val input = tx.inputsOfType<LandTitleState>().single()
        val output = tx.outputsOfType<LandTitleState>().single()

        "There should be exactly one input state" using(tx.inputs.size == 1)
        "There should be exactly one output state" using(tx.outputs.size == 1)
        "Status of Consumed LandTitle state must be `PENDING_BUYER_APPROVAL`" using (input.status == Status.PENDING_BUYER_APPROVAL)
        "Status of Updated LandTitle state must be `TRANSFERRED`" using(output.status == Status.TRANSFERRED)
        "Buyer and Accepting party must be null in the output state" using (output.landTitleProperties.buyer == null && output.landTitleProperties.buyerConveyancer == null)
        val verify = Crypto.doVerify(input.landTitleProperties.buyer!!.publicKey!!, output.landTitleProperties.owner!!.signature!!, input.titleID.toByteArray())
        "Only Land Title owner can initiate the Land Transfer Request" using(verify == true)
        "Owner in the output state should be updated with correct owner value" using (output.landTitleProperties.owner.copy(signature = input.landTitleProperties.buyer.signature) == input.landTitleProperties.buyer && output.landTitleProperties.ownerConveyancer == input.landTitleProperties.buyerConveyancer)
        "Mismatch in the input and output states" using(input == output.copy(status = input.status, landTitleProperties = output.landTitleProperties.copy(owner = input.landTitleProperties.owner, ownerConveyancer = input.landTitleProperties.ownerConveyancer, buyer = input.landTitleProperties.buyer, buyerConveyancer = input.landTitleProperties.buyerConveyancer)))

    }
}