package com.hmlr.flows

import com.hmlr.model.*
import com.hmlr.states.LandTitleState
import com.hmlr.states.RequestIssuanceState
import net.corda.core.concurrent.CordaFuture
import net.corda.core.crypto.Crypto
import net.corda.core.identity.CordaX500Name
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.core.singleIdentity
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNodeParameters
import net.corda.testing.node.StartedMockNode
import org.junit.After
import org.junit.Before
import java.security.PrivateKey

/**
 * A base class to reduce the boilerplate when writing land title flow tests.
 */
abstract class LandTitleTests {
    lateinit var mockNetwork: MockNetwork
    lateinit var issuer: StartedMockNode
    lateinit var sellerConveyancer: StartedMockNode
    lateinit var buyerConveyancer: StartedMockNode
    lateinit var hmrc: StartedMockNode

    val buyerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val sellerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val buyerPublicKey = buyerKeys.public
    val buyerPrivateKey = buyerKeys.private
    val sellerPublicKey = sellerKeys.public
    val sellerPrivateKey = sellerKeys.private
    val titleId = "ZQV888860"
    val invalidTitleId = "0000000000"
    val location = Address("A1-S2", "GreenBank Road", "Devon", "Plymouth", "UK", "PL6 5ZD")
    val seller = CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nits7sid@example.com", "9422327871", true, signature = null, publicKey = sellerPublicKey)



    @Before
    fun setup() {
        mockNetwork = MockNetwork(listOf("com.hmlr.states", "com.hmlr.contracts", "com.hmlr.flows", "com.hmlr.schema"))
        issuer = mockNetwork.createNode(MockNodeParameters(legalName = CordaX500Name("HMLR", "Plymouth", "GB")))
        sellerConveyancer = mockNetwork.createNode(MockNodeParameters(legalName = CordaX500Name("Owner", "London", "GB")))
        buyerConveyancer = mockNetwork.createNode(MockNodeParameters(legalName = CordaX500Name("Buyer", "London", "GB")))
        hmrc = mockNetwork.createNode(MockNodeParameters(legalName = CordaX500Name("HMRC", "London", "GB")))
        mockNetwork.runNetwork()
        listOf(issuer, sellerConveyancer, buyerConveyancer).forEach { it.registerInitiatedFlow(IssueLandTitle::class.java) }
    }

    @After
    fun tearDown() {
        mockNetwork.stopNodes()
    }

    protected fun sign(message: String, key: PrivateKey): ByteArray {
        return Crypto.doSign(Crypto.RSA_SHA256, key, message.toByteArray())
    }

    protected fun requestForIssuance() : SignedTransaction? {
        val issuer = issuer.info.singleIdentity()
        val conveyancer = sellerConveyancer.info.singleIdentity()

        val requestIssuanceState = RequestIssuanceState(titleId, issuer, conveyancer, seller, RequestIssuanceStatus.PENDING)
        val flow = RequestIssuanceFlow(requestIssuanceState)
        val future = sellerConveyancer.startFlow(flow)
        mockNetwork.runNetwork()
        val signdTx = future.getOrThrow()
        val recordedTx = sellerConveyancer.services.validatedTransactions.getTransaction(signdTx.id)
        return recordedTx
    }

    protected fun requestForIssuanceFailed() : SignedTransaction? {
        val issuer = issuer.info.singleIdentity()
        val conveyancer = sellerConveyancer.info.singleIdentity()

        val requestIssuanceState = RequestIssuanceState(invalidTitleId, issuer, conveyancer, seller, RequestIssuanceStatus.PENDING)
        val flow = RequestIssuanceFlow(requestIssuanceState)
        val future = sellerConveyancer.startFlow(flow)
        mockNetwork.runNetwork()
        val signdTx = future.getOrThrow()
        val recordedTx = sellerConveyancer.services.validatedTransactions.getTransaction(signdTx.id)
        return recordedTx
    }

    protected fun landTitleTransferRequest(): CordaFuture<SignedTransaction>? {
        val location = Address("A1-S2","GreenBank Road","Devon","PL6", "UK", "PL6 5ZD")
        val buyingParty  = CustomParty("Nitesh", "Solanki", "125464", location, UserType.INDIVIDUAL, "nitesh@example.com","9876543210",true,publicKey = buyerPublicKey,signature = null)
        val acceptingParty = buyerConveyancer.info.singleIdentity()
        requestForIssuance()
        mockNetwork.waitQuiescent()
        return sellerConveyancer.transaction {
            val txOutputs = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            val recordedState = txOutputs[0].state.data
            val transferFlow = TransferLandTitleRequest(recordedState.linearId.toString(), buyingParty, acceptingParty, sign(recordedState.titleID, sellerPrivateKey))
            val future = sellerConveyancer.startFlow(transferFlow)
            mockNetwork.runNetwork()
            future
        }
    }

    protected fun landTitleTransferResponse(): CordaFuture<SignedTransaction>? {
        requestForIssuance()
        landTitleTransferRequest()
        mockNetwork.waitQuiescent()
        return buyerConveyancer.transaction {
            val txOutputs = buyerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            val recordedState = txOutputs[0].state.data
            val responseFlow = TransferLandTitleResponse(recordedState.linearId.toString(),sign(recordedState.titleID, buyerPrivateKey))
            val future = buyerConveyancer.startFlow(responseFlow)
            mockNetwork.runNetwork()
            future
        }
    }
}
