package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.states.PaymentConfirmationState
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction

/**
 * Sub-flow to sign the land title transfer
 */
@InitiatedBy(InitiateLandTitleTransferFlow::class)
class AcceptLandTitleTransferFlow(val otherPartyFlow: FlowSession) : FlowLogic<SignedTransaction>() {

    /**
     * The flows logic is encapsulated within the call() method.
     */
    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(otherPartyFlow) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                // for now all checks are made in the contract
            }
        }
        return subFlow(signTransactionFlow)
    }
}