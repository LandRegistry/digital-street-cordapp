package com.hmlr.flows

import com.hmlr.states.InstructConveyancerState
import net.corda.core.transactions.SignedTransaction
import org.junit.Test
import kotlin.test.assertFailsWith

class InstructConveyancerFlowTests : AbstractFlowTestUtils() {

    @Test
    fun `instruct conveyancer successfully`() {
        //execute the instruct conveyancer flow
        instructConveyancer(issuer)
        mockNetwork.waitQuiescent()

        //confirm the state exists on HMLR node
        issuer.transaction {
            val states = issuer.services.vaultService.queryBy(InstructConveyancerState::class.java).states
            assert(states.size == 1)
        }

        //confirm the state exists on the seller conveyancer node
        sellerConveyancer.transaction {
            val states = issuer.services.vaultService.queryBy(InstructConveyancerState::class.java).states
            assert(states.size == 1)
        }
    }

    @Test
    fun `only the issuer can execute the instruct conveyancer flow`() {
        assertFailsWith<SignedTransaction.SignaturesMissingException> { instructConveyancer(buyerConveyancer) }
    }

}