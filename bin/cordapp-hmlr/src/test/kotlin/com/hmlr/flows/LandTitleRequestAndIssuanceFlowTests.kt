package com.hmlr.flows

import com.hmlr.model.RequestIssuanceStatus
import com.hmlr.model.Status
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

class LandTitleRequestAndIssuanceFlowTests: LandTitleTests() {

    @Test
    fun `request land title issuance and issue land title automatically`() {
        // send request for issuance and automatically issue the land title
        val tx = requestForIssuance()

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
            assert(states[0].state.data.status ==  Status.ISSUED)
        }

        issuer.transaction {
            val states = issuer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  Status.ISSUED)
        }

    }

    @Test
    fun `request for land title issuance can only made by conveyancer`() {
        val titleIssuer = issuer.info.singleIdentity()
        val conveyancer = sellerConveyancer.info.singleIdentity()
        val requestIssuanceState = RequestIssuanceState(titleId, conveyancer, titleIssuer, seller, RequestIssuanceStatus.PENDING)
        val flow = RequestIssuanceFlow(requestIssuanceState)
        val future = hmrc.startFlow(flow)
        mockNetwork.runNetwork()
        assertFailsWith<SignedTransaction.SignaturesMissingException> { future.getOrThrow() }
    }

    @Test
    fun `title issuer and requesting party cannot be same` () {
        val conveyancer = sellerConveyancer.info.singleIdentity()
        val requestIssuanceState = RequestIssuanceState(titleId, conveyancer, conveyancer, seller, RequestIssuanceStatus.PENDING)
        val flow = RequestIssuanceFlow(requestIssuanceState)
        val future =sellerConveyancer.startFlow(flow)
        mockNetwork.runNetwork()
        assertFailsWith<TransactionVerificationException> { future.getOrThrow() }
    }

    @Test
    fun `issuance must fail if title id not found`() {
        // send request for issuance and fail to issue the land title
        val tx = requestForIssuanceFailed()

        // log the issuance request state which would be in 'PENDING' state
        val state = tx!!.tx.outputs[0].data as RequestIssuanceState
        print(state)

        // confirm request issuance state with status as 'FAILED' on both conveyancer as well as HMLR node
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

        // confirm [LandTitleState] on Conveyancer as well as HMLR node
        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 0)
        }

        issuer.transaction {
            val states = issuer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 0)
        }
    }

    @Test
    fun `reject already issued land title request` (){
        // send request for issuance and automatically issue the land title
        val tx = requestForIssuance()

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
            assert(states[0].state.data.status ==  Status.ISSUED)
        }

        issuer.transaction {
            val states = issuer.services.vaultService.queryBy(LandTitleState::class.java).states
            assert(states.size == 1)
            assert(states[0].state.data.status ==  Status.ISSUED)
        }

        // re-send the request for issuance for same titleID
        // log the issuance request state which would be in 'PENDING' state
        val resendTx = requestForIssuance()
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