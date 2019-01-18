package com.hmlr.contracts

import com.hmlr.states.InstructConveyancerState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

/**
 * Contract code to govern the [InstructConveyancerState]
 * [Commands.CreateInstruction] Command logic verifies create instruction transaction
 * [Commands.AcceptInstruction] Command logic verifies accept instruction transaction
 */
class InstructConveyancerContract: Contract {

    /**
     * Define Contract ID
     */
    companion object {
        @JvmStatic
        val INSTRUCT_CONVEYANCER_CONTRACT_ID = InstructConveyancerContract::class.java.name!!
    }

    /**
     * List of all commands classes supported by the [InstructConveyancerContract]
     */
    interface Commands : CommandData {
        class CreateInstruction : TypeOnlyCommandData(), InstructConveyancerContract.Commands
        class AcceptInstruction : TypeOnlyCommandData(), InstructConveyancerContract.Commands
    }

    /**
     * Pure [Boolean] function to verify the incoming transaction
     * @param tx [LedgerTransaction]
     * @return [Boolean]
     */
    override fun verify(tx: LedgerTransaction) {
        val commands = tx.commands.requireSingleCommand<InstructConveyancerContract.Commands>()
        val setOfSigners = commands.signers.toSet()
        when (commands.value) {
            is InstructConveyancerContract.Commands.CreateInstruction -> verifyInstructConveyancer(tx, setOfSigners)
            is InstructConveyancerContract.Commands.AcceptInstruction -> verifyInstructConveyancerAccept(tx, setOfSigners)
            else -> throw IllegalArgumentException("Unrecognised Command")
        }
    }

    /**
     * validation logic for [Commands.CreateInstruction] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyInstructConveyancer(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {
        "No inputs should be consumed while instructing a conveyancer" using(tx.inputs.isEmpty())

        "There should be exactly one output state" using(tx.outputs.size == 1)
        val output = tx.outputsOfType<InstructConveyancerState>().single()
        "Conveyancer and Title Issuer must be different" using(output.titleIssuer.owningKey != output.conveyancer.owningKey)
        "Owner's identity must have been verified" using(output.user.confirmationOfIdentity == true)
        "Instruction request must only be signed by the title issuer" using(setOfSigners.contains((output.titleIssuer.owningKey)) && setOfSigners.size == 1)
        "Only title issuer and conveyancer must be in the list of participants" using(output.participants.containsAll(listOf(output.conveyancer, output.titleIssuer)) && output.participants.size == 2)
    }

    /**
     * validation logic for [Commands.AcceptInstruction] command
     * @param tx [LedgerTransaction]
     * @param setOfSigners [Set]
     * @return [Boolean]
     */
    private fun verifyInstructConveyancerAccept(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) = requireThat {
        "There should be exactly one input state" using(tx.inputs.size == 1)
        val input = tx.inputsOfType<InstructConveyancerState>().single()

        "There should be exactly one output state" using(tx.outputs.size == 1)
        "Instruction acceptance must be signed by instructed conveyancer" using(setOfSigners.contains(input.conveyancer.owningKey) && setOfSigners.size == 1)
    }
}