package com.hmlr.contracts

import com.hmlr.model.*
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import com.hmlr.states.LandTitleState
import com.hmlr.states.LandAgreementState
import net.corda.core.crypto.Crypto
import java.security.PublicKey
import java.time.Instant
import java.time.ZoneOffset

/**
 * Contract code to govern the [LandAgreementState]
 * [Commands.CreateDraftAgreement] Command logic verifies create draft agreement transaction
 * [Commands.ApproveSalesAgreement] Command logic verifies approve agreement transaction
 * [Commands.SellerSignAgreement] Command logic verifies seller sign agreement transaction
 * [Commands.BuyerSignAgreement] Command logic verifies buyer sign agreement transaction
 * [Commands.FinalizeAgreement] Command logic verifies finalize agreement transaction
 *
 */
class LandAgreementContract: Contract {

    /**
     * Define Contract ID
     */
    companion object {
        @JvmStatic
        val LAND_AGREEMENT_CONTRACT_ID = LandAgreementContract::class.java.name!!
    }

    /**
     * List of all commands classes supported by the [LandAgreementContract]
     */
    interface Commands : CommandData {
        class CreateDraftAgreement : TypeOnlyCommandData(), LandAgreementContract.Commands
        class ApproveSalesAgreement : TypeOnlyCommandData(), LandAgreementContract.Commands
        class SellerSignAgreement : TypeOnlyCommandData(), LandAgreementContract.Commands
        class BuyerSignAgreement : TypeOnlyCommandData(), LandAgreementContract.Commands
        class FinalizeAgreement : TypeOnlyCommandData(), LandAgreementContract.Commands
    }

    /**
     * Pure [Boolean] function to verify the incoming transaction
     * @param tx [LedgerTransaction]
     * @return [Boolean]
     */
    override fun verify(tx: LedgerTransaction) {
        val commands = tx.commands.requireSingleCommand<Commands>()
        val setOfSigners = commands.signers.toSet()
        when (commands.value) {
            is Commands.CreateDraftAgreement -> verifyCreateDraftAgreement(tx, setOfSigners)
            is Commands.ApproveSalesAgreement -> verifyApproveSalesAgreement(tx, setOfSigners)
            is Commands.SellerSignAgreement -> verifySellerSignAgreement(tx)
            is Commands.BuyerSignAgreement -> verifyBuyerSignAgreement(tx)
            is Commands.FinalizeAgreement -> verifyFinalizeAgreement(tx, setOfSigners)
            else -> throw IllegalArgumentException("Unrecognised Command")
        }
    }

    /**
     * validation logic for [Commands.CreateDraftAgreement] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyCreateDraftAgreement(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {
        "Two input should be consumed while creating draft agreement" using (tx.inputs.size == 2)
        val inputLandTitleState = tx.inputsOfType<LandTitleState>().single()

        "There should be exactly three output states" using (tx.outputs.size == 3)
        val outputLandAgreementState = tx.outputsOfType<LandAgreementState>().single()
        val timeWindow: TimeWindow? = tx.timeWindow
        val currentTime = timeWindow?.fromTime ?: throw IllegalArgumentException("Drafting must have a time window")
        val currentDate = Instant.ofEpochMilli(currentTime.toEpochMilli()).atZone(ZoneOffset.UTC).toLocalDate()
        val creationDateInstant = outputLandAgreementState.creationDate.atStartOfDay().toInstant(ZoneOffset.UTC)

        "Transaction must be signed by the seller's conveyancer" using (setOfSigners.size == 1 && setOfSigners.contains(outputLandAgreementState.sellerConveyancer.owningKey))
        "Seller must be the owner of the consumed land title state" using (outputLandAgreementState.seller == inputLandTitleState.landTitleProperties.owner)
        "Drafting conveyancer must be the owner's conveyancer" using (outputLandAgreementState.sellerConveyancer == inputLandTitleState.landTitleProperties.ownerConveyancer)
        "Contract creation date cannot be in the past" using (currentDate <= outputLandAgreementState.creationDate)
        "Completion date cannot be in the past" using (currentTime <= outputLandAgreementState.completionDate)
        "Completion date cannot be before creation date" using (creationDateInstant <= outputLandAgreementState.completionDate)
        "Seller and buyer cannot be the same" using (outputLandAgreementState.seller != outputLandAgreementState.buyer)
        "Title state linearID must be the same as the consumed land title state" using (outputLandAgreementState.titleStateLinearId == inputLandTitleState.linearId.toString())
        "Land agreement titleID must be the same as the consumed land title state titleID" using (outputLandAgreementState.titleID == inputLandTitleState.titleID)
        "Deposit amount must be less than purchase price" using (outputLandAgreementState.deposit < outputLandAgreementState.purchasePrice)
        "Balance must be equal to the purchase price minus the deposit" using (outputLandAgreementState.balance == outputLandAgreementState.purchasePrice - outputLandAgreementState.deposit)
        "Seller's conveyancer and buyer's conveyancer cannot be the same" using (outputLandAgreementState.sellerConveyancer != outputLandAgreementState.buyerConveyancer)
        "Buyer's conveyancer and seller's conveyancer should be in the Land Agreement participants list" using (outputLandAgreementState.participants.size == 2 && outputLandAgreementState.participants.containsAll(listOf(outputLandAgreementState.sellerConveyancer, outputLandAgreementState.buyerConveyancer)))
        "isMortgageTermsAdded flag must be false" using(!outputLandAgreementState.isMortgageTermsAdded)
        "The agreement state status should be 'CREATED'" using (outputLandAgreementState.status == AgreementStatus.CREATED)
    }

    /**
     * validation logic for [Commands.ApproveSalesAgreement] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyApproveSalesAgreement(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {
        "One input should be consumed while approving sales agreement" using (tx.inputs.size == 1)
        val input = tx.inputsOfType<LandAgreementState>().single()
        "Input agreement status should be 'CREATED'" using (input.status == AgreementStatus.CREATED)

        "There should be exactly one output state while approving sales agreement" using (tx.outputs.size == 1)
        val output = tx.outputsOfType<LandAgreementState>().single()
        "Transaction must be signed by the buyer's conveyancer" using (setOfSigners.size == 1 && setOfSigners.contains(output.buyerConveyancer.owningKey))
        "Output agreement status should be 'APPROVED'" using (output.status == AgreementStatus.APPROVED)
        "isMortgageTermsAdded flag must be true" using(output.isMortgageTermsAdded)
        "Mismatch in input and output states while approving sales agreement" using (input == output.copy(status = AgreementStatus.CREATED, participants = input.participants, isMortgageTermsAdded = input.isMortgageTermsAdded))
    }


    /**
     * validation logic for [Commands.SellerSignAgreement] command
     * @param tx [LedgerTransaction]
     * @return [Boolean]
     */
    private fun verifySellerSignAgreement(tx: LedgerTransaction) = requireThat {
        "One input should be consumed while approving draft agreement" using (tx.inputs.size == 1)
        val input = tx.inputsOfType<LandAgreementState>().single()
        "Input agreement status should be 'APPROVED'" using (input.status == AgreementStatus.APPROVED)

        "There should be exactly one output state" using (tx.outputs.size == 1)
        val output = tx.outputsOfType<LandAgreementState>().single()
        val verify = Crypto.doVerify(input.seller.publicKey!!, output.seller.signature!!, input.titleID.toByteArray())
        "Only the seller can sign the agreement" using (verify == true)
        "Output agreement status should be 'SIGNED'" using (output.status == AgreementStatus.SIGNED)
        "Mismatch in input and output states" using (input == output.copy(status = AgreementStatus.APPROVED, participants = input.participants, seller = input.seller))
    }

    /**
     * validation logic for [Commands.BuyerSignAgreement] command
     * @param tx [LedgerTransaction]
     * @return [Boolean]
     */
    private fun verifyBuyerSignAgreement(tx: LedgerTransaction) = requireThat {
        "One input should be consumed while approving draft agreement" using (tx.inputs.size == 1)
        val input = tx.inputsOfType<LandAgreementState>().single()
        "Input agreement status should be 'SIGNED'" using (input.status == AgreementStatus.SIGNED)

        "There should be exactly one output state" using (tx.outputs.size == 1)
        val output = tx.outputsOfType<LandAgreementState>().single()
        val verify = Crypto.doVerify(input.buyer.publicKey!!, output.buyer.signature!!, input.titleID.toByteArray())
        "Only the buyer can sign the agreement" using (verify == true)
        "Output agreement status should be 'COMPLETED'" using (output.status == AgreementStatus.COMPLETED)
        "Mismatch in input and output states" using (input == output.copy(status = input.status, buyer = input.buyer, seller = input.seller))
        // for ByteArray comparison we need to use equals instead of ==
        "Signature of seller should not be updated" using(java.util.Arrays.equals(input.seller.signature, output.seller.signature))
    }

    /**
     * validation logic for [Commands.FinalizeAgreement] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyFinalizeAgreement(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "Three input states must be consumed" using(tx.inputs.size == 3)
        "Three output states must be produced" using(tx.outputs.size == 3)

        val inputAgreementState = tx.inputsOfType<LandAgreementState>().single()
        val inputLandTitleState = tx.inputsOfType<LandTitleState>().single()

        val outputAgreementState = tx.outputsOfType<LandAgreementState>().single()
        "Transaction must be signed by both the conveyancer" using(setOfSigners.containsAll(listOf(inputAgreementState.sellerConveyancer.owningKey, inputAgreementState.buyerConveyancer.owningKey, inputLandTitleState.titleIssuer.owningKey)))
        "Consumed Agreement state must be in 'COMPLETED' state" using(inputAgreementState.status == AgreementStatus.COMPLETED)
        "Produced Agreement state must be in 'TRANSFERRED' state'" using(outputAgreementState.status == AgreementStatus.TRANSFERRED)
        "Title state linearID must be the same as the consumed land title state" using (inputAgreementState.titleStateLinearId == inputLandTitleState.linearId.toString())
        "Land agreement titleID must be the same as the consumed land title state titleID" using (inputAgreementState.titleID == inputLandTitleState.titleID)
        "Mismatch in the input and output LandAgreement state" using(inputAgreementState == outputAgreementState.copy(status = inputAgreementState.status, seller = outputAgreementState.seller.copy(signature = inputAgreementState.seller.signature), buyer = outputAgreementState.buyer.copy(signature = inputAgreementState.buyer.signature)))
    }
}