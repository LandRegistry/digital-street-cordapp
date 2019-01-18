package com.hmlr

import com.hmlr.flows.*
import com.hmlr.model.*
import com.hmlr.states.*
import net.corda.client.rpc.CordaRPCClient
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startFlow
import net.corda.core.messaging.vaultTrackBy
import net.corda.core.node.services.Vault
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
                    Permissions.startFlow<InstructConveyancerFlow>(),
                    Permissions.invokeRpc("vaultQuery"),
                    Permissions.invokeRpc("vaultTrackBy"),
                    Permissions.invokeRpc("wellKnownPartyFromX500Name"),
                    Permissions.invokeRpc(CordaRPCOps::notaryIdentities),
                    Permissions.invokeRpc(CordaRPCOps::networkMapFeed)
            ))

            // define Conveyancer RPC User
            val conveyancerUser = User("conveyancerUser", "testPassword2", permissions = setOf(
                    Permissions.startFlow<RequestIssuanceFlow>(),
                    Permissions.startFlow<DraftAgreementFlow>(),
                    Permissions.startFlow<ApproveAgreementFlow>(),
                    Permissions.startFlow<BuyerSignAgreementFlow>(),
                    Permissions.startFlow<SellerSignAgreementFlow>(),
                    Permissions.startFlow<InitiateLandTitleTransferFlow>(),
                    Permissions.startFlow<RequestForDischargeFlow>(),
                    Permissions.startFlow<AddNewChargeFlow>(),
                    Permissions.startFlow<ConsentForDischargeFlow>(),
                    Permissions.invokeRpc("vaultQuery"),
                    Permissions.invokeRpc("vaultTrackBy"),
                    Permissions.invokeRpc("wellKnownPartyFromX500Name"),
                    Permissions.invokeRpc(CordaRPCOps::notaryIdentities),
                    Permissions.invokeRpc(CordaRPCOps::networkMapFeed)
            ))
            // start 3 corda nodes
            val (hmlrHandle, conveyancerAHandle, conveyancerBHandle, lenderA, lenderB) = listOf(
                    startNode(providedName = HMLR, rpcUsers = listOf(hmlrUser)),
                    startNode(providedName = ConveyancerA, rpcUsers = listOf(conveyancerUser)),
                    startNode(providedName = ConveyancerB, rpcUsers = listOf(conveyancerUser)),
                    startNode(providedName = LenderA, rpcUsers = listOf(conveyancerUser)),
                    startNode(providedName = LenderB, rpcUsers = listOf(conveyancerUser))

            ).map { it.getOrThrow() }

            val hmlrClient = CordaRPCClient(hmlrHandle.rpcAddress)
            val hmlrProxy = hmlrClient.start("hmlrUser", "testPassword1").proxy

            val conveyancerAClient = CordaRPCClient(conveyancerAHandle.rpcAddress)
            val conveyancerAProxy = conveyancerAClient.start("conveyancerUser", "testPassword2").proxy

            val conveyancerBClient = CordaRPCClient(conveyancerBHandle.rpcAddress)
            val conveyancerBProxy = conveyancerBClient.start("conveyancerUser", "testPassword2").proxy

            val lenderAClient = CordaRPCClient(lenderA.rpcAddress)
            val lenderAProxy = lenderAClient.start("conveyancerUser", "testPassword2").proxy

            val conveyancerAVaultUpdates = conveyancerAProxy.vaultTrackBy<LandTitleState>().updates

            // STEP: 1
            // call instruct conveyancer flow from HMLR node
            val instructConveyancerState = InstructConveyancerState(titleID = titleId, caseReferenceNumber = caseRefNum, titleIssuer = hmlrHandle.nodeInfo.legalIdentities[0], conveyancer = conveyancerAHandle.nodeInfo.legalIdentities[0], user = seller)
            hmlrProxy.startFlow(::InstructConveyancerFlow, instructConveyancerState).returnValue.getOrThrow()

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            val instructConveyancerStateFromVault = hmlrProxy.vaultQuery(InstructConveyancerState::class.java)
            val instructionStateLinearID = instructConveyancerStateFromVault.states[0].state.data.linearId.toString()

            // STEP: 2
            // call request issuance flow
            val requestIssuanceState = RequestIssuanceState(titleID = titleId, titleIssuer = hmlrHandle.nodeInfo.legalIdentities[0], seller = seller, sellerConveyancer = conveyancerAHandle.nodeInfo.legalIdentities[0], status = RequestIssuanceStatus.PENDING, instructionStateLinearID = instructionStateLinearID)
            conveyancerAProxy.startFlow(::RequestIssuanceFlow, requestIssuanceState, instructionStateLinearID)

            // listen for LandTitleState update
            conveyancerAVaultUpdates.expectEvents {
                parallel(
                        net.corda.testing.core.expect { update: Vault.Update<LandTitleState> -> println(update) }
                )
            }

            // STEP: 3
            // start request for discharge
            var landTitleState = conveyancerAProxy.vaultQuery(LandTitleState::class.java)
            var landTitleStateLinearId = landTitleState.states[0].state.data.linearId.toString()
            conveyancerAProxy.startFlow(::RequestForDischargeFlow, landTitleStateLinearId)

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
            landTitleState = conveyancerAProxy.vaultQuery(LandTitleState::class.java)
            landTitleStateLinearId = landTitleState.states[0].state.data.linearId.toString()
            completionDate = Instant.now().plusSeconds(60)
            val agreementState = LandAgreementState(titleId, buyer, seller, conveyancerBHandle.nodeInfo.legalIdentities.get(0), conveyancerAHandle.nodeInfo.legalIdentities.get(0), creationDate, completionDate!!, 9.0, 1000.POUNDS, 50.POUNDS, null, 950.POUNDS, landTitleStateLinearId, listOf(), TitleGuarantee.FULL, AgreementStatus.CREATED, false)
            conveyancerAProxy.startFlow(::DraftAgreementFlow, agreementState, conveyancerBHandle.nodeInfo.legalIdentities.get(0))

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 6
            // start consent for new charge
            val proposedChargeRestrictionState = conveyancerBProxy.vaultQuery(ProposedChargesAndRestrictionsState::class.java)
            val proposedChargeRestrictionStateLinearId = proposedChargeRestrictionState.states[0].state.data.linearId.toString()
            val charge = Charge(Instant.now(), lenderA.nodeInfo.legalIdentities[0], 100.POUNDS)
            val chargeRestriction = ChargeRestriction("CBCR", restrictionText, lenderA.nodeInfo.legalIdentities[0], ActionOnRestriction.ADD_RESTRICTION, true, charge)
            conveyancerBProxy.startFlow(::AddNewChargeFlow, proposedChargeRestrictionStateLinearId, setOf(chargeRestriction), setOf(charge))

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 7
            // Approve sales agreement
            val conveyancerAagreementState = conveyancerAProxy.vaultQuery(LandAgreementState::class.java)
            val agreementStateLinearId = conveyancerAagreementState.states[0].state.data.linearId.toString()
            conveyancerBProxy.startFlow(::ApproveAgreementFlow, agreementStateLinearId)

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 8
            // Seller signs sales agreement
            conveyancerAProxy.startFlow(::SellerSignAgreementFlow, agreementStateLinearId, sign(titleId, sellerPrivateKey))

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(5000)

            // STEP: 9
            // Buyer signs sales agreement
            conveyancerBProxy.startFlow(::BuyerSignAgreementFlow, agreementStateLinearId, sign(titleId, buyerPrivateKey))

            // wait for 5sec for transaction to get committed on vault
            Thread.sleep(60000)

            // STEP: 10
            // Query for Land Title state to check for new owner if everything goes right
            val landTitleStateAfterTransfer = conveyancerBProxy.vaultQuery(LandTitleState::class.java)
            assertEquals(landTitleStateAfterTransfer.states[0].state.data.status, LandTitleStatus.TRANSFERRED)
            assertEquals(landTitleStateAfterTransfer.states[0].state.data.landTitleProperties.owner.userID, buyer.userID)

            // Query for Land Agreement state
            val landAgreementState = conveyancerBProxy.vaultQuery(LandAgreementState::class.java)
            assertEquals(landAgreementState.states[0].state.data.status, AgreementStatus.TRANSFERRED)
        }
    }
}