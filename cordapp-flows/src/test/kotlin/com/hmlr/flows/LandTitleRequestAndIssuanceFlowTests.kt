package com.hmlr.flows

import com.hmlr.model.LandTitleStatus
import com.hmlr.model.RequestIssuanceStatus
import com.hmlr.states.LandTitleState
import com.hmlr.states.RequestIssuanceState
import net.corda.core.contracts.TransactionVerificationException
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.Sort
import net.corda.core.node.services.vault.SortAttribute
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.core.singleIdentity
import org.junit.Test
import kotlin.test.assertFailsWith

class LandTitleRequestAndIssuanceFlowTests : AbstractFlowTestUtils() {

    @Test
    fun `request land title issuance and issue land title automatically`() {
        // send request for issuance and automatically issue the land title
        val tx = requestForIssuance()!!.tx
        mockNetwork.waitQuiescent()

        // log the issuance request state which would be in 'PENDING' state
        val state = tx.outputs[0].data as RequestIssuanceState
        print(state)

        // confirm request issuance state with status as 'APPROVED' on both conveyancer as well as HMLR node
        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(RequestIssuanceState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  RequestIssuanceStatus.APPROVED)
        }

        issuer.transaction {
            val states = issuer.services.vaultService.queryBy(RequestIssuanceState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  RequestIssuanceStatus.APPROVED)
        }

        // confirm [LandTitleState] on Conveyancer as well as HMLR node
        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  LandTitleStatus.ISSUED)
        }

        issuer.transaction {
            val states = issuer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  LandTitleStatus.ISSUED)
        }

    }

    @Test
    fun `request for land title issuance can only made by conveyancer`() {
        val titleIssuer = issuer.info.singleIdentity()
        val conveyancer = sellerConveyancer.info.singleIdentity()
        val requestIssuanceState = RequestIssuanceState(titleId, titleIssuer, conveyancer, seller, RequestIssuanceStatus.PENDING)
        val flow = RequestIssuanceFlow(requestIssuanceState)
        val future = issuer.startFlow(flow)
        assertFailsWith<SignedTransaction.SignaturesMissingException> { future.getOrThrow() }
    }

    @Test
    fun `title issuer and requesting party cannot be same` () {
        val conveyancer = sellerConveyancer.info.singleIdentity()
        val requestIssuanceState = RequestIssuanceState(titleId, conveyancer, conveyancer, seller, RequestIssuanceStatus.PENDING)
        val flow = RequestIssuanceFlow(requestIssuanceState)
        val future =sellerConveyancer.startFlow(flow)
        assertFailsWith<TransactionVerificationException> { future.getOrThrow() }
    }

    @Test
    fun `issuance must fail if title id not found`() {
        requestForIssuanceFailed()
        mockNetwork.waitQuiescent()
        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(RequestIssuanceState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  RequestIssuanceStatus.FAILED)
        }

        issuer.transaction {
            val states = issuer.services.vaultService.queryBy(RequestIssuanceState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  RequestIssuanceStatus.FAILED)
        }
    }

    @Test
    fun `reject already issued land title request` (){
        // send request for issuance and automatically issue the land title
        val tx = requestForIssuance()
        mockNetwork.waitQuiescent()

        // log the issuance request state which would be in 'PENDING' state
        val state = tx!!.tx.outputs[0].data as RequestIssuanceState
        print(state)

        // confirm request issuance state with status as 'APPROVED' on both conveyancer as well as HMLR node
        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(RequestIssuanceState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  RequestIssuanceStatus.APPROVED)
        }

        issuer.transaction {
            val states = issuer.services.vaultService.queryBy(RequestIssuanceState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  RequestIssuanceStatus.APPROVED)
        }

        // confirm [LandTitleState] on Conveyancer as well as HMLR node
        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  LandTitleStatus.ISSUED)
        }

        issuer.transaction {
            val states = issuer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  LandTitleStatus.ISSUED)
        }

        // re-send the request for issuance for same titleID
        // log the issuance request state which would be in 'PENDING' state
        val resendTx = requestForIssuance()
        mockNetwork.waitQuiescent()
        val outputState = resendTx!!.tx.outputs[0].data as RequestIssuanceState
        print(outputState)

        // confirm request issuance state with status as 'TITLE_ALREADY_ISSUED' on both conveyancer as well as HMLR node
        val generalCriteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED)
        val sortAttribute = SortAttribute.Standard(Sort.VaultStateAttribute.RECORDED_TIME)
        val sortBy = Sort(listOf(Sort.SortColumn(sortAttribute, Sort.Direction.DESC)))
        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(RequestIssuanceState::class.java, generalCriteria, sortBy).states
            assert(states[0].state.data.status == RequestIssuanceStatus.TITLE_ALREADY_ISSUED)
        }

        issuer.transaction {
            val states = issuer.services.vaultService.queryBy(RequestIssuanceState::class.java, generalCriteria, sortBy).states
            assert(states[0].state.data.status == RequestIssuanceStatus.TITLE_ALREADY_ISSUED)
        }
    }
}