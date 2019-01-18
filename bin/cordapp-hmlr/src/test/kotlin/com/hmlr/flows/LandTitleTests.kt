package com.hmlr.flows

import com.hmlr.model.*
import com.hmlr.states.LandAgreementState
import com.hmlr.states.LandTitleState
import com.hmlr.states.RequestIssuanceState
import net.corda.core.crypto.Crypto
import net.corda.core.identity.CordaX500Name
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.finance.POUNDS
import net.corda.testing.core.singleIdentity
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNodeParameters
import net.corda.testing.node.StartedMockNode
import org.junit.After
import org.junit.Before
import java.security.PrivateKey
import java.time.LocalDate

/**
 * A base class to reduce the boilerplate when writing land title flow tests.
 */
abstract class LandTitleTests {
    lateinit var mockNetwork: MockNetwork
    lateinit var issuer: StartedMockNode
    lateinit var sellerConveyancer: StartedMockNode
    lateinit var buyerConveyancer: StartedMockNode
    lateinit var hmrc: StartedMockNode

    val wrongUser = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val buyerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val sellerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val buyerPublicKey = buyerKeys.public
    val buyerPrivateKey = buyerKeys.private
    val sellerPublicKey = sellerKeys.public
    val sellerPrivateKey = sellerKeys.private
    val titleId = "ZQV888860"
    val invalidTitleId = "0000000000"
    val location = Address("10", "Digital Street", "Avon", "Bristol", "United Kingdom", "BS2 8EN")
    val seller = CustomParty("Lisa", "White", "1230", location, UserType.INDIVIDUAL, "Lisa.White@example.com", "07123456780", true, signature = null, publicKey = sellerPublicKey)
    val buyer = CustomParty("David", "Jones", "125464", location, UserType.INDIVIDUAL, "buyer@example.com", "0123456789", true, signature = null, publicKey = buyerPublicKey)



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

    protected fun createDraftAgreement(callingParty: StartedMockNode): SignedTransaction? {
        requestForIssuance()
        mockNetwork.waitQuiescent()

        // fetch the titleID of the land title state from seller conveyancer
        var titleID: String? = null
        var owner: CustomParty? = null

        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            titleID = states[0].state.data.linearId.toString()
            owner = states[0].state.data.landTitleProperties.owner!!
        }
        val ownerConveyancer = sellerConveyancer.info.singleIdentity()
        val buyerConveyancer = buyerConveyancer.info.singleIdentity()
        val agreementState = LandAgreementState(titleId, buyer, owner!!, buyerConveyancer, ownerConveyancer, LocalDate.now(), LocalDate.now().plusDays(14), 9.0, 1000.POUNDS, 50.POUNDS, null, 950.POUNDS, titleID!!, listOf(), TitleGuarantee.FULL, AgreementStatus.CREATED)
        val flow = DraftAgreement(agreementState, buyerConveyancer)
        val future = callingParty.startFlow(flow)
        mockNetwork.runNetwork()
        return future.getOrThrow()
    }

    protected fun approveLandAgreement(callingParty: StartedMockNode): SignedTransaction {
        createDraftAgreement(sellerConveyancer)
        mockNetwork.waitQuiescent()

        // fetch the linearID of the agreement state from seller conveyancer
        var linearID: String? = null

        callingParty.transaction {
            val states = callingParty.services.vaultService.queryBy(LandAgreementState::class.java).states
            linearID = states[0].state.data.linearId.toString()
        }

        val flow = ApproveAgreement(linearID!!)
        val future = callingParty.startFlow(flow)
        mockNetwork.runNetwork()
        return future.getOrThrow()
    }

    protected fun sellerSignAgreement(callingParty: StartedMockNode, privateKey: PrivateKey): SignedTransaction {
        approveLandAgreement(buyerConveyancer)
        mockNetwork.waitQuiescent()

        // fetch the linearID of the agreement state from seller conveyancer
        var linearID: String? = null

        callingParty.transaction {
            val states = callingParty.services.vaultService.queryBy(LandAgreementState::class.java).states
            linearID = states[0].state.data.linearId.toString()
        }

        val flow = SellerSignAgreement(linearID!!, sign(titleId, privateKey))
        val future = callingParty.startFlow(flow)
        mockNetwork.runNetwork()
        return future.getOrThrow()
    }

    protected fun buyerSignAgreement(callingParty: StartedMockNode, privateKey: PrivateKey): SignedTransaction {
        sellerSignAgreement(sellerConveyancer, sellerPrivateKey)
        mockNetwork.waitQuiescent()

        // fetch the linearID of the agreement state from buyer conveyancer
        var linearID: String? = null

        callingParty.transaction {
            val states = callingParty.services.vaultService.queryBy(LandAgreementState::class.java).states
            linearID = states[0].state.data.linearId.toString()
        }

        val flow = BuyerSignAgreement(linearID!!, sign(titleId, privateKey))
        val future = callingParty.startFlow(flow)
        mockNetwork.runNetwork()
        return future.getOrThrow()
    }
}
