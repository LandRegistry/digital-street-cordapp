package com.hmlr.flows

import com.hmlr.model.*
import com.hmlr.states.*
import net.corda.core.crypto.Crypto
import net.corda.core.identity.CordaX500Name
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.finance.POUNDS
import net.corda.testing.core.singleIdentity
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNodeParameters
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.TestClock
import org.junit.After
import org.junit.Before
import java.security.PrivateKey
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * A base class to reduce the boilerplate when writing land title flow tests.
 */
abstract class AbstractFlowTestUtils {
    lateinit var mockNetwork: MockNetwork
    lateinit var issuer: StartedMockNode
    lateinit var sellerConveyancer: StartedMockNode
    lateinit var buyerConveyancer: StartedMockNode
    lateinit var sellerLender: StartedMockNode
    lateinit var hmrc: StartedMockNode
    lateinit var buyerLender: StartedMockNode
    lateinit var settlingParty: StartedMockNode

    val wrongUser = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val buyerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val sellerKeys = Crypto.generateKeyPair(Crypto.RSA_SHA256)
    val buyerPublicKey = buyerKeys.public
    val buyerPrivateKey = buyerKeys.private
    val sellerPublicKey = sellerKeys.public
    val sellerPrivateKey = sellerKeys.private
    val titleId = "ZQV888860"
    val caseRefNum = "ABCD123"
    val invalidTitleId = "0000000000"
    val location = Address("10", "Digital Street", "Bristol", "Bristol", "England", "BS2 8EN")
    val seller = CustomParty("Lisa", "White", "1", location, UserType.INDIVIDUAL, "lisa.white@example.com", "07700900354", true, signature = null, publicKey = sellerPublicKey)
    val buyer = CustomParty("David", "Jones", "125464", location, UserType.INDIVIDUAL, "buyer@example.com", "0123456789", true, signature = null, publicKey = buyerPublicKey)
    var completionDate: Instant? = null
    val creationDate = LocalDate.now()



    @Before
    fun setup() {
        mockNetwork = MockNetwork(listOf("com.hmlr.states", "com.hmlr.contracts", "com.hmlr.flows", "com.hmlr.schema"), threadPerNode = true)
        issuer = mockNetwork.createNode(MockNodeParameters(legalName = CordaX500Name("HMLR", "Plymouth", "GB")))
        sellerConveyancer = mockNetwork.createNode(MockNodeParameters(legalName = CordaX500Name("Owner", "London", "GB")))
        buyerConveyancer = mockNetwork.createNode(MockNodeParameters(legalName = CordaX500Name("Buyer", "London", "GB")))
        sellerLender = mockNetwork.createNode(MockNodeParameters(legalName = CordaX500Name("Lender1", "Plymouth", "GB")))
        hmrc = mockNetwork.createNode(MockNodeParameters(legalName = CordaX500Name("HMRC", "Plymouth", "GB")))
        buyerLender = mockNetwork.createNode(MockNodeParameters(legalName = CordaX500Name("Lender2", "Plymouth", "GB")))
        settlingParty = mockNetwork.createNode(MockNodeParameters(legalName = CordaX500Name("SettlingParty", "Plymouth", "GB")))
        mockNetwork.startNodes()
        listOf(issuer, sellerConveyancer).forEach {
            it.registerInitiatedFlow(IssueLandTitleFlow::class.java)
            it.registerInitiatedFlow(AcceptLandTitleTransferFlow::class.java)
        }
    }

    @After
    fun tearDown() {
        mockNetwork.stopNodes()
    }

    private fun sign(message: String, key: PrivateKey): ByteArray {
        return Crypto.doSign(Crypto.RSA_SHA256, key, message.toByteArray())
    }


    protected fun requestForIssuance() : SignedTransaction? {

        val issuer = issuer.info.singleIdentity()
        val conveyancer = sellerConveyancer.info.singleIdentity()

        val requestIssuanceState = RequestIssuanceState(titleId, issuer, conveyancer, seller, RequestIssuanceStatus.PENDING)
        val flow = RequestIssuanceFlow(requestIssuanceState)
        val future = sellerConveyancer.startFlow(flow)
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
        val signdTx = future.getOrThrow()
        val recordedTx = sellerConveyancer.services.validatedTransactions.getTransaction(signdTx.id)
        return recordedTx
    }

    protected fun createDraftAgreement(callingParty: StartedMockNode): SignedTransaction? {
        completionDate = creationDate.atStartOfDay().plusDays(14).toInstant(ZoneOffset.UTC)

        consentForDischarge(sellerLender)
        mockNetwork.waitQuiescent()

        // fetch the titleID of the land title state from seller conveyancer
        var titleID: String? = null
        var owner: CustomParty? = null

        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            titleID = states[0].state.data.linearId.toString()
            owner = states[0].state.data.landTitleProperties.owner
        }
        val ownerConveyancer = sellerConveyancer.info.singleIdentity()
        val buyerConveyancer = buyerConveyancer.info.singleIdentity()
        val agreementState = LandAgreementState(titleId, buyer, owner!!, buyerConveyancer, ownerConveyancer, creationDate, completionDate!!, 9.0, 1000.POUNDS, 50.POUNDS, null, 950.POUNDS, titleID!!, listOf(), TitleGuarantee.FULL, AgreementStatus.CREATED, false, "")
        val flow = DraftAgreementFlow(agreementState, buyerConveyancer, settlingParty.info.singleIdentity())
        val future = callingParty.startFlow(flow)
        return future.getOrThrow()
    }

    protected fun approveLandAgreement(callingParty: StartedMockNode): SignedTransaction {
        addNewCharge(buyerConveyancer)
        mockNetwork.waitQuiescent()

        // fetch the linearID of the agreement state from seller conveyancer
        var linearID: String? = null

        callingParty.transaction {
            val states = callingParty.services.vaultService.queryBy(LandAgreementState::class.java).states
            linearID = states[0].state.data.linearId.toString()
        }

        val flow = ApproveAgreementFlow(linearID!!)
        val future = callingParty.startFlow(flow)
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

        val flow = SellerSignAgreementFlow(linearID!!, sign(titleId, privateKey))
        val future = callingParty.startFlow(flow)
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

        confirmFundsReceivedInEscrow()
        mockNetwork.waitQuiescent()

        val flow = BuyerSignAgreementFlow(linearID!!, sign(titleId, privateKey))
        // adjust the date of the nodes to completion date for testing purpose
        listOf(sellerConveyancer, buyerConveyancer).forEach {
            (it.services.clock as TestClock).setTo(completionDate!!)
        }

        val future = callingParty.startFlow(flow)
        return future.getOrThrow()
    }

    protected fun requestForDischarge(callingParty: StartedMockNode): SignedTransaction? {
        requestForIssuance()
        mockNetwork.waitQuiescent()

        // fetch the titleID of the land title state from seller conveyancer
        var linearID: String? = null

        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            linearID = states[0].state.data.linearId.toString()
        }
        val flow = RequestForDischargeFlow(linearID!!)
        val future = callingParty.startFlow(flow)
        return future.getOrThrow()
    }

    protected fun consentForDischarge(callingParty: StartedMockNode): SignedTransaction? {
        requestForDischarge(sellerConveyancer)
        mockNetwork.waitQuiescent()

        // fetch the titleID of the land title state from seller conveyancer
        var linearID: String? = null

        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            linearID = states[0].state.data.linearId.toString()
        }
        val flow = ConsentForDischargeFlow(linearID!!)
        val future = callingParty.startFlow(flow)
        return future.getOrThrow()
    }

    protected fun addNewCharge(callingParty: StartedMockNode): SignedTransaction? {
        val restrictionText = "No disposition of the registered estate by the proprietor of the registered estate is to be registered"
        val charge = Charge(Instant.now(), sellerLender.info.singleIdentity(), 100.POUNDS)
        val chargeRestriction = ChargeRestriction("CBCR", restrictionText, sellerLender.info.singleIdentity(), ActionOnRestriction.ADD_RESTRICTION, true, charge)

        createDraftAgreement(sellerConveyancer)
        mockNetwork.waitQuiescent()

        // fetch the proposedChargeOrRestrictionLinearID of the land title state from seller conveyancer
        var proposedChargeOrRestrictionLinearID: String? = null

        sellerConveyancer.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(LandTitleState::class.java).states
            proposedChargeOrRestrictionLinearID = states[0].state.data.proposedChargeOrRestrictionLinearId.toString()
        }
        val flow = AddNewChargeFlow(proposedChargeOrRestrictionLinearID!!, setOf(chargeRestriction), setOf(charge))
        val future = callingParty.startFlow(flow)
        return future.getOrThrow()
    }

    protected fun confirmFundsReceivedInEscrow(): SignedTransaction? {
        // fetch the titleID of the land title state from seller conveyancer
        var linearID: String? = null

        settlingParty.transaction {
            val states = sellerConveyancer.services.vaultService.queryBy(PaymentConfirmationState::class.java).states
            linearID = states[0].state.data.linearId.toString()
        }
        val flow = ConfirmPaymentReceivedFlow(linearID!!)
        val future = settlingParty.startFlow(flow)
        return future.getOrThrow()
    }
}
