package com.hmlr.contracts

import com.hmlr.model.RequestIssuanceStatus
import com.hmlr.states.InstructConveyancerState
import com.hmlr.states.RequestIssuanceState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import org.bouncycastle.util.encoders.Hex
import java.security.PublicKey
import java.security.Signature

/**
 * Contract code to govern the [RequestIssuanceState]
 * [Commands.RequestIssuance] Command logic verifies request issuance transaction
 * [Commands.ApproveRequest] Command logic verifies approve issuance transaction
 * [Commands.IssuanceFailed] Command logic verifies failed issuance transaction. This transaction can occur when there is no response from Title API in Land Title Issuance sub-flow.
 * [Commands.RequestIssuanceIfFailed] Command logic verifies retry request for issuance transaction
 * [Commands.Reject] Command logic verifies reject issuance transaction
 */
class RequestIssuanceContract: Contract {

    /**
     * Define Contract ID
     */
    companion object {
        @JvmStatic
        val REQUEST_ISSUANCE_CONTRACT_ID = RequestIssuanceContract::class.java.name!!
    }

    /**
     * List of all commands classes supported by the [RequestIssuanceContract]
     */
    interface Commands: CommandData {
        class RequestIssuance: TypeOnlyCommandData(), Commands
        class ApproveRequest: TypeOnlyCommandData(), Commands
        class IssuanceFailed: TypeOnlyCommandData(), Commands
        class RequestIssuanceIfFailed: TypeOnlyCommandData(), Commands
        class Reject: TypeOnlyCommandData(), Commands
    }

    /**
     * Pure [Boolean] function to verify the incoming transaction
     * @param tx [LedgerTransaction]
     * @return [Boolean]
     */
    override fun verify(tx: LedgerTransaction) {
        val commands = tx.commands.requireSingleCommand<RequestIssuanceContract.Commands>()
        val setOfSigners = commands.signers.toSet()
        when (commands.value) {
            is RequestIssuanceContract.Commands.RequestIssuance -> verifyRequestIssuance(tx, setOfSigners)
            is RequestIssuanceContract.Commands.ApproveRequest -> verifyApproveRequest(tx, setOfSigners)
            is RequestIssuanceContract.Commands.IssuanceFailed -> verifyIssuanceFailed(tx, setOfSigners)
            is RequestIssuanceContract.Commands.RequestIssuanceIfFailed -> verifyRequestIssuanceIfFailed(tx, setOfSigners)
            is RequestIssuanceContract.Commands.Reject -> verifyReject(tx, setOfSigners)
            else -> throw IllegalArgumentException("Unrecognised Command")
        }
    }

    /**
     * validation logic for [Commands.RequestIssuance] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyRequestIssuance(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "One input should be consumed when requesting issuance of a title on the ledger" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<InstructConveyancerState>().single()

        "There should be exactly one output state when requesting issuance of a title on the ledger" using(tx.outputs.size == 1)
        val output = tx.outputsOfType<RequestIssuanceState>().single()
        "Issuing party and conveyancer party must be different when requesting issuance of a title on the ledger" using (output.titleIssuer != output.sellerConveyancer)
        "Title id in the instruction state and request issuance state should be equal when requesting issuance of a title on the ledger" using(input.titleID == output.titleID)
        "Request issuance state must have a linear Id of the consumed instruction state when requesting issuance of a title on the ledger" using(output.instructionStateLinearID == input.linearId.toString())
        "Request for issuance must only be signed by the conveyancer" using(setOfSigners.contains((output.sellerConveyancer.owningKey)) && setOfSigners.size == 1)
        "Only title issuer and conveyancer must be added to the list of participants when requesting issuance of a title on the ledger" using(output.participants.containsAll(listOf(output.sellerConveyancer, output.titleIssuer)) && output.participants.size == 2)
        "Request issuance status must be 'PENDING' when requesting issuance of a title on the ledger" using(output.status == RequestIssuanceStatus.PENDING)
    }

    /**
     * validation logic for [Commands.ApproveRequest] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyApproveRequest(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "There should be exactly one input state when approving issuance request" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<RequestIssuanceState>().single()
        "Consumed issuance request status must be 'PENDING' when approving issuance request" using(input.status == RequestIssuanceStatus.PENDING)

        "There should be exactly three output states when approving issuance request" using(tx.outputs.size == 3)
        val requestIssuanceOutput = tx.outputsOfType<RequestIssuanceState>().single()
        "Issuance request must be approved by the titleIssuer" using(setOfSigners.contains(input.titleIssuer.owningKey))
        "Status of issuance request in the output must be 'APPROVED' when approving issuance request" using(requestIssuanceOutput.status == RequestIssuanceStatus.APPROVED)
        "Mismatch in the issuance request input and output states" using(input == requestIssuanceOutput.copy(status = RequestIssuanceStatus.PENDING))
    }

    /**
     * validation logic for [Commands.IssuanceFailed] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyIssuanceFailed(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "There should be exactly one input state" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<RequestIssuanceState>().single()
        "Consumed issuance request status must be 'PENDING'" using(input.status == RequestIssuanceStatus.PENDING)

        "There should be exactly one output state" using(tx.outputs.size == 1)
        val requestIssuanceOutput = tx.outputsOfType<RequestIssuanceState>().single()
        "Issuance request must be approved by the titleIssuer" using(setOfSigners.contains(input.titleIssuer.owningKey))
        "Status of issuance request in the output must be 'FAILED'" using(requestIssuanceOutput.status == RequestIssuanceStatus.FAILED)
        "Mismatch in the issuance request input and output states" using(input == requestIssuanceOutput.copy(status = RequestIssuanceStatus.PENDING))
    }

    /**
     * validation logic for [Commands.RequestIssuanceIfFailed] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyRequestIssuanceIfFailed(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "There should be exactly one input state when requesting for issuance" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<RequestIssuanceState>().single()
        "Consumed issuance request status must be 'FAILED' when requesting for issuance" using(input.status == RequestIssuanceStatus.FAILED)

        "There should be exactly one output state when requesting for issuance" using(tx.outputs.size == 1)
        val output = tx.outputsOfType<RequestIssuanceState>().single()
        "Issuing party and conveyancer party must be different when requesting for issuance" using (output.titleIssuer != output.sellerConveyancer)
        "Request for issuance must only be signed by the conveyancer" using(setOfSigners.contains((output.sellerConveyancer.owningKey)) && setOfSigners.size == 1)
        "Only title issuer and conveyancer must be added to the list of participants when requesting for issuance" using(output.participants.containsAll(listOf(output.sellerConveyancer, output.titleIssuer)) && output.participants.size == 2)
        "Status of issuance request state in the output must be 'PENDING' when requesting for issuance" using(output.status == RequestIssuanceStatus.PENDING)
    }

    /**
     * validation logic for [Commands.Reject] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyReject(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "There should be exactly one input state when rejecting issuance request" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<RequestIssuanceState>().single()
        "Consumed issuance request status must be 'PENDING'" using(input.status == RequestIssuanceStatus.PENDING)

        "There should be exactly one output state when rejecting issuance request" using(tx.outputs.size == 1)
        val output = tx.outputsOfType<RequestIssuanceState>().single()
        "Issuing party and conveyancer party must be different when rejecting issuance request" using (output.titleIssuer != output.sellerConveyancer)
        "Issuance request must be rejected by the titleIssuer" using(setOfSigners.contains(input.titleIssuer.owningKey))
        "Only title issuer and conveyancer must be added to the list of participants when rejecting issuance request" using(output.participants.containsAll(listOf(output.sellerConveyancer, output.titleIssuer)) && output.participants.size == 2)
        "Request issuance Status must be 'TITLE_ALREADY_ISSUED' after rejecting issuance request" using(output.status == RequestIssuanceStatus.TITLE_ALREADY_ISSUED)
    }
}