package com.hmlr

import com.hmlr.flows.*
import com.hmlr.model.*
import com.hmlr.states.*
import net.corda.client.rpc.CordaRPCClient
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startFlow
import net.corda.core.messaging.vaultTrackBy
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.Sort
import net.corda.core.utilities.getOrThrow
import net.corda.finance.POUNDS
import net.corda.node.services.Permissions
import net.corda.testing.core.expectEvents
import net.corda.testing.core.parallel
import net.corda.testing.driver.DriverParameters
import net.corda.testing.driver.driver
import net.corda.testing.node.User
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

class IntegrationTest: AbstractIntegrationTestUtils() {

    @Test
    fun `run end to end tests for land registration and conveyancing process`() {
        driver(DriverParameters(
                isDebug = false,
                startNodesInProcess = true,
                extraCordappPackagesToScan = listOf("com.hmlr.states", "com.hmlr.contracts", "com.hmlr.flows", "com.hmlr.schema")
                )) {

            // define HMLR RPC User
            val hmlrUser = User("hmlrUser", "testPassword1", permissions = setOf(
                    Permissions.invokeRpc("vaultQuery"),
                    Permissions.invokeRpc("vaultTrackBy"),
                    Permissions.invokeRpc("wellKnownPartyFromX500Name"),
                    Permissions.invokeRpc(CordaRPCOps::notaryIdentities),
                    Permissions.invokeRpc(CordaRPCOps::networkMapFeed)
            ))

            // define Conveyancer RPC User
            val commonUser = User("commonUser", "testPassword2", permissions = setOf(
                    Permissions.startFlow<RequestIssuanceFlow>(),
                    Permissions.startFlow<DraftAgreementFlow>(),
                    Permissions.startFlow<ApproveAgreementFlow>(),
                    Permissions.startFlow<BuyerSignAgreementFlow>(),
                    Permissions.startFlow<SellerSignAgreementFlow>(),
                    Permissions.startFlow<InitiateLandTitleTransferFlow>(),
                    Permissions.startFlow<RequestForDischargeFlow>(),
                    Permissions.startFlow<AddNewChargeFlow>(),
                    Permissions.startFlow<ConsentForDischargeFlow>(),
                    Permissions.startFlow<ConfirmPaymentReceivedFlow>(),
                    Permissions.invokeRpc("vaultQuery"),
                    Permissions.invokeRpc("vaultQueryBy"),
                    Permissions.invokeRpc("vaultTrackBy"),
                    Permissions.invokeRpc("wellKnownPartyFromX500Name"),
                    Permissions.invokeRpc(CordaRPCOps::notaryIdentities),
                    Permissions.invokeRpc(CordaRPCOps::networkMapFeed)
            ))
         
            val (hmlrHandle, conveyancerAHandle, conveyancerBHandle, lenderA, settlingPartyHandle) = listOf(
                    startNode(providedName = HMLR, rpcUsers = listOf(hmlrUser)),
                    startNode(providedName = ConveyancerA, rpcUsers = listOf(commonUser)),
                    startNode(providedName = ConveyancerB, rpcUsers = listOf(commonUser)),
                    startNode(providedName = LenderA, rpcUsers = listOf(commonUser)),
                    startNode(providedName = SettlingParty, rpcUsers = listOf(commonUser))

            ).map { it.getOrThrow() }

            val conveyancerAClient = CordaRPCClient(conveyancerAHandle.rpcAddress)
            val conveyancerAProxy = conveyancerAClient.start("commonUser", "testPassword2").proxy

            val conveyancerBClient = CordaRPCClient(conveyancerBHandle.rpcAddress)
            val conveyancerBProxy = conveyancerBClient.start("commonUser", "testPassword2").proxy

            val lenderAClient = CordaRPCClient(lenderA.rpcAddress)
            val lenderAProxy = lenderAClient.start("commonUser", "testPassword2").proxy

            val settlingPartyClient = CordaRPCClient(settlingPartyHandle.rpcAddress)
            val settlingPartyProxy = settlingPartyClient.start("commonUser", "testPassword2").proxy

            val conveyancerAVaultUpdates = conveyancerAProxy.vaultTrackBy<LandTitleState>().updates

            // STEP: 1

            // call request issuance flow
            val requestIssuanceState = RequestIssuanceState(titleID = titleId, titleIssuer = hmlrHandle.nodeInfo.legalIdentities[0], seller = seller, sellerConveyancer = conveyancerAHandle.nodeInfo.legalIdentities[0], status = RequestIssuanceStatus.PENDING )
            conveyancerAProxy.startFlow(::RequestIssuanceFlow, requestIssuanceState)

            // listen for LandTitleState update
            conveyancerAVaultUpdates.expectEvents {
                parallel(
                        net.corda.testing.core.expect { update: Vault.Update<LandTitleState> -> println(update) }
                )
            }

            // STEP: 2
            // start request for discharge
            var landTitleState = conveyancerAProxy.vaultQuery(LandTitleState::class.java)
            var landTitleStateLinearId = landTitleState.states[0].state.data.linearId.toString()
            conveyancerAProxy.startFlow(::RequestForDischargeFlow, landTitleStateLinearId)

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 3
            // start consent for discharge
            landTitleState = lenderAProxy.vaultQuery(LandTitleState::class.java)
            landTitleStateLinearId = landTitleState.states[0].state.data.linearId.toString()
            lenderAProxy.startFlow(::ConsentForDischargeFlow, landTitleStateLinearId)

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 4
            // start draft agreement flow
            landTitleState = conveyancerAProxy.vaultQuery(LandTitleState::class.java)
            landTitleStateLinearId = landTitleState.states[0].state.data.linearId.toString()
            completionDate = Instant.now().plusSeconds(60)
            var agreementState = LandAgreementState(titleId, buyer, seller, conveyancerBHandle.nodeInfo.legalIdentities.get(0), conveyancerAHandle.nodeInfo.legalIdentities.get(0), creationDate, completionDate!!, 9.0, 1000.POUNDS, 50.POUNDS, null, 950.POUNDS, landTitleStateLinearId, listOf(), TitleGuarantee.FULL, AgreementStatus.CREATED, false, "")
            conveyancerAProxy.startFlow(::DraftAgreementFlow, agreementState, conveyancerBHandle.nodeInfo.legalIdentities.get(0), settlingPartyHandle.nodeInfo.legalIdentities.get(0))

            // wait for 60sec for transaction to get committed on vault
            Thread.sleep(60000)

            // STEP: 5
            // start consent for new charge
            var proposedChargeRestrictionState = conveyancerBProxy.vaultQuery(ProposedChargesAndRestrictionsState::class.java)
            var proposedChargeRestrictionStateLinearId = proposedChargeRestrictionState.states[0].state.data.linearId.toString()
            var charge = Charge(Instant.now(), lenderA.nodeInfo.legalIdentities[0], 100.POUNDS)
            var chargeRestriction = ChargeRestriction("CBCR", restrictionText, lenderA.nodeInfo.legalIdentities[0], ActionOnRestriction.ADD_RESTRICTION, true, charge)
            conveyancerBProxy.startFlow(::AddNewChargeFlow, proposedChargeRestrictionStateLinearId, setOf(chargeRestriction), setOf(charge))

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 6
            // Approve sales agreement
            val conveyancerAagreementState = conveyancerAProxy.vaultQuery(LandAgreementState::class.java)
            val agreementStateLinearId = conveyancerAagreementState.states[0].state.data.linearId.toString()
            conveyancerBProxy.startFlow(::ApproveAgreementFlow, agreementStateLinearId)

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 7
            // Seller signs sales agreement
            conveyancerAProxy.startFlow(::SellerSignAgreementFlow, agreementStateLinearId, sign(titleId, sellerPrivateKey))

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 8
            var paymentConfirmationState = settlingPartyProxy.vaultQuery(PaymentConfirmationState::class.java)
            val paymentConfirmationStateLinearId = paymentConfirmationState.states[0].state.data.linearId.toString()
            settlingPartyProxy.startFlow(::ConfirmPaymentReceivedFlow, paymentConfirmationStateLinearId)

            Thread.sleep(60000)

            // STEP: 9
            // Buyer signs sales agreement
            conveyancerBProxy.startFlow(::BuyerSignAgreementFlow, agreementStateLinearId, sign(titleId, buyerPrivateKey))

            // wait for 60sec for transaction to get committed on vault
            Thread.sleep(60000)
            // STEP: 10
            // Query for Land Title state to check for new owner if everything goes right
            var landTitleStateAfterTransfer = conveyancerBProxy.vaultQuery(LandTitleState::class.java)
            assertEquals(landTitleStateAfterTransfer.states[0].state.data.status, LandTitleStatus.TRANSFERRED)
            assertEquals(landTitleStateAfterTransfer.states[0].state.data.landTitleProperties.owner.userID, buyer.userID)

            // Query for Land Agreement state
            var landAgreementState = conveyancerBProxy.vaultQuery(LandAgreementState::class.java)
            assertEquals(landAgreementState.states[0].state.data.status, AgreementStatus.TRANSFERRED)

            // transfer the land title back to the seller

            // start request for discharge
            landTitleState = conveyancerBProxy.vaultQuery(LandTitleState::class.java)
            landTitleStateLinearId = landTitleState.states[0].state.data.linearId.toString()
            conveyancerBProxy.startFlow(::RequestForDischargeFlow, landTitleStateLinearId)

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 4
            // start consent for discharge
            landTitleState = lenderAProxy.vaultQuery(LandTitleState::class.java)
            landTitleStateLinearId = landTitleState.states[0].state.data.linearId.toString()
            lenderAProxy.startFlow(::ConsentForDischargeFlow, landTitleStateLinearId)

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 5
            // start draft agreement flow
            landTitleState = conveyancerBProxy.vaultQuery(LandTitleState::class.java)
            landTitleStateLinearId = landTitleState.states[0].state.data.linearId.toString()
            completionDate = Instant.now().plusSeconds(60)
            agreementState = LandAgreementState(titleId, seller, buyer, conveyancerAHandle.nodeInfo.legalIdentities.get(0), conveyancerBHandle.nodeInfo.legalIdentities.get(0), creationDate, completionDate!!, 9.0, 1000.POUNDS, 50.POUNDS, null, 950.POUNDS, landTitleStateLinearId, listOf(), TitleGuarantee.FULL, AgreementStatus.CREATED, false, "")
            val agreementId = agreementState.linearId.toString()
            conveyancerBProxy.startFlow(::DraftAgreementFlow, agreementState, conveyancerAHandle.nodeInfo.legalIdentities.get(0), settlingPartyHandle.nodeInfo.legalIdentities.get(0))

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 6
            // start consent for new charge
            proposedChargeRestrictionState = conveyancerAProxy.vaultQuery(ProposedChargesAndRestrictionsState::class.java)
            proposedChargeRestrictionStateLinearId = proposedChargeRestrictionState.states[0].state.data.linearId.toString()
            charge = Charge(Instant.now(), lenderA.nodeInfo.legalIdentities[0], 100.POUNDS)
            chargeRestriction = ChargeRestriction("CBCR", restrictionText, lenderA.nodeInfo.legalIdentities[0], ActionOnRestriction.ADD_RESTRICTION, true, charge)
            conveyancerAProxy.startFlow(::AddNewChargeFlow, proposedChargeRestrictionStateLinearId, setOf(chargeRestriction), setOf(charge))

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 7
            // Approve sales agreement
            conveyancerBProxy.vaultQuery(LandAgreementState::class.java)
            conveyancerAProxy.startFlow(::ApproveAgreementFlow, agreementId)

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 8
            // Seller signs sales agreement
            conveyancerBProxy.startFlow(::SellerSignAgreementFlow, agreementId, sign(titleId, buyerPrivateKey))

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 9
            val criteria = QueryCriteria.LinearStateQueryCriteria(contractStateTypes = setOf(LandAgreementState::class.java), linearId = listOf(agreementState.linearId))
            val newAgreementState = conveyancerBProxy.vaultQueryBy(criteria, contractStateType = LandAgreementState::class.java, paging = PageSpecification(), sorting = Sort(listOf()))

            paymentConfirmationState = settlingPartyProxy.vaultQuery(PaymentConfirmationState::class.java)
            paymentConfirmationState.states[0].state.data.linearId.toString()
            settlingPartyProxy.startFlow(::ConfirmPaymentReceivedFlow, newAgreementState.states[0].state.data.paymentConfirmationStateLinearId)

            Thread.sleep(60000)

            // STEP: 10
            // Buyer signs sales agreement
            conveyancerAProxy.startFlow(::BuyerSignAgreementFlow, agreementId, sign(titleId, sellerPrivateKey))

            // wait for 60sec for transaction to get committed on vault
            Thread.sleep(60000)

            // STEP: 11
            // Query for Land Title state to check for new owner if everything goes right
            landTitleStateAfterTransfer = conveyancerAProxy.vaultQuery(LandTitleState::class.java)
            assertEquals(landTitleStateAfterTransfer.states[0].state.data.status, LandTitleStatus.TRANSFERRED)
            assertEquals(landTitleStateAfterTransfer.states[0].state.data.landTitleProperties.owner.userID, seller.userID)

            // Query for Land Agreement state
            landAgreementState = conveyancerAProxy.vaultQuery(LandAgreementState::class.java)
            assertEquals(landAgreementState.states[0].state.data.status, AgreementStatus.TRANSFERRED)
        }
    }
}