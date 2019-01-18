package com.hmlr.contracts

import com.hmlr.model.RequestIssuanceStatus
import com.hmlr.states.RequestIssuanceState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

/**
 * Land Title Contract class with rules/business logic definition which governs the evolution of [RequestIssuanceState]
 *
 **/
class RequestIssuanceContract: Contract {

    companion object {
        @JvmStatic
        val REQUEST_ISSUANCE_CONTRACT_ID = RequestIssuanceContract::class.java.name!!
    }

    interface Commands: CommandData {
        class RequestIssuance: TypeOnlyCommandData(), Commands
        class ApproveRequest: TypeOnlyCommandData(), Commands
        class IssuanceFailed: TypeOnlyCommandData(), Commands
        class RequestIssuanceIfFailed: TypeOnlyCommandData(), Commands
        class Reject: TypeOnlyCommandData(), Commands
    }

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

    private fun verifyRequestIssuance(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "No inputs should be consumed while request issuance of a title on the ledger" using(tx.inputs.isEmpty())
        "There should be exactly one output state" using(tx.outputs.size == 1)

        val output = tx.outputsOfType<RequestIssuanceState>().single()
        "Issuing party and conveyancer party must be different" using (output.titleIssuer != output.sellerConveyancer)
        "Issuance request must only be signed by the conveyancer" using(setOfSigners.contains((output.sellerConveyancer.owningKey)) && setOfSigners.size == 1)
        "Only title issuer and conveyancer must be in the list of participants" using(output.participants.containsAll(listOf(output.sellerConveyancer, output.titleIssuer)) && output.participants.size == 2)
        "Status must be 'PENDING'" using(output.status == RequestIssuanceStatus.PENDING)
    }

    private fun verifyApproveRequest(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "There should be exactly one input state" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<RequestIssuanceState>().single()
        "Issuance request input status must be 'PENDING'" using(input.status == RequestIssuanceStatus.PENDING)

        "There should be exactly two output states" using(tx.outputs.size == 2)
        val requestIssuanceOutput = tx.outputsOfType<RequestIssuanceState>().single()
        "Issuance request must be approved by the titleIssuer" using(setOfSigners.contains(input.titleIssuer.owningKey))
        "Issuance request output status must be 'APPROVED'" using(requestIssuanceOutput.status == RequestIssuanceStatus.APPROVED)
        "Mismatch in the issuance request input and output states" using(input == requestIssuanceOutput.copy(status = RequestIssuanceStatus.PENDING))
    }

    private fun verifyIssuanceFailed(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "There should be exactly one input state" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<RequestIssuanceState>().single()
        "Input issuance request input status must be 'PENDING'" using(input.status == RequestIssuanceStatus.PENDING)

        "There should be exactly one output state" using(tx.outputs.size == 1)
        val requestIssuanceOutput = tx.outputsOfType<RequestIssuanceState>().single()
        "Issuance request output must be approved by the titleIssuer" using(setOfSigners.contains(input.titleIssuer.owningKey))
        "Issuance request output status must be 'FAILED'" using(requestIssuanceOutput.status == RequestIssuanceStatus.FAILED)
        "Mismatch in the issuance request input and output states" using(input == requestIssuanceOutput.copy(status = RequestIssuanceStatus.PENDING))
    }

    private fun verifyRequestIssuanceIfFailed(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "There should be exactly one input state" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<RequestIssuanceState>().single()
        "Issuance request input status must be 'FAILED'" using(input.status == RequestIssuanceStatus.FAILED)

        "There should be exactly one output state" using(tx.outputs.size == 1)
        val output = tx.outputsOfType<RequestIssuanceState>().single()
        "Issuing party and conveyancer party must be different" using (output.titleIssuer != output.sellerConveyancer)
        "Issuance request output must only be signed by the conveyancer" using(setOfSigners.contains((output.sellerConveyancer.owningKey)) && setOfSigners.size == 1)
        "Only title issuer and conveyancer must be in the list of participants" using(output.participants.containsAll(listOf(output.sellerConveyancer, output.titleIssuer)) && output.participants.size == 2)
        "Issuance request output status must be 'PENDING'" using(output.status == RequestIssuanceStatus.PENDING)
    }

    private fun verifyReject(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat{
        "There should be exactly one input state" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<RequestIssuanceState>().single()
        "Issuance request input status must be 'PENDING'" using(input.status == RequestIssuanceStatus.PENDING)
        "There should be exactly one output state" using(tx.outputs.size == 1)

        val output = tx.outputsOfType<RequestIssuanceState>().single()
        "Issuing party and conveyancer party must be different" using (output.titleIssuer != output.sellerConveyancer)
        "Issuance request must be rejected by the titleIssuer" using(setOfSigners.contains(input.titleIssuer.owningKey))
        "Only title issuer and conveyancer must be in the list of participants" using(output.participants.containsAll(listOf(output.sellerConveyancer, output.titleIssuer)) && output.participants.size == 2)
        "Status must be 'TITLE_ALREADY_ISSUED'" using(output.status == RequestIssuanceStatus.TITLE_ALREADY_ISSUED)
    }
}