package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.contracts.LandTitleContract
import com.hmlr.contracts.RequestIssuanceContract
import com.hmlr.model.*
import com.hmlr.schema.LandTitleStateSchemaV1
import com.hmlr.states.LandTitleState
import com.hmlr.states.RequestIssuanceState
import net.corda.core.contracts.Command
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step
import net.corda.core.utilities.unwrap
import khttp.get
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.QueryCriteria
import java.io.FileReader
import java.util.*

/**
 * This is a sub-flow which is executed on HMLR node and it will issue a Land Title on the Corda ledger.
 * The flow makes an API call to the mocked registry to fetch the title data.
 */
@InitiatedBy(RequestIssuanceFlow::class)
class IssueLandTitle(val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {

    companion object {

        object RECEIVE_REQUEST_TRANSACTION : Step("Receive Issuance Request Transaction ID from the requesting party")
        object WAIT_REQUEST_TRANSACTION_TO_COMMIT : Step("Wait for transaction to get commit")
        object GENERATING_LAND_TITLE_ISSUE_TRANSACTION : Step("Generate Land Title Issuance transaction")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints.")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with our private key.")

        object FINALISING_LAND_TITLE_ISSUE_TRANSACTION : Step("Obtaining notary signature and recording transaction.") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(
                RECEIVE_REQUEST_TRANSACTION,
                WAIT_REQUEST_TRANSACTION_TO_COMMIT,
                GENERATING_LAND_TITLE_ISSUE_TRANSACTION,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                FINALISING_LAND_TITLE_ISSUE_TRANSACTION
        )
    }

    override val progressTracker = tracker()

    /**
     * The flows logic is encapsulated within the call() method.
     */
    @Suspendable
    override fun call(): SignedTransaction {

        // obtain a reference to the notary
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        // STEP: 1
        progressTracker.currentStep = RECEIVE_REQUEST_TRANSACTION
        val txID = flowSession.receive<SecureHash>().unwrap { secureHash -> secureHash }

        // STEP: 2
        progressTracker.currentStep = WAIT_REQUEST_TRANSACTION_TO_COMMIT
        val requestTx = waitForLedgerCommit(txID)
        val requestIssuanceState = requestTx.tx.outputStates.single() as RequestIssuanceState

        // STEP: 3
        progressTracker.currentStep = GENERATING_LAND_TITLE_ISSUE_TRANSACTION
        val tx = TransactionBuilder(notary)

        // add input state
        val inputRequestIssuanceState = requestTx.tx.outRefsOfType<RequestIssuanceState>().single()
        tx.addInputState(inputRequestIssuanceState)

        // check if Land Title already issued on the ledger
        if (checkIfTitleAlreadyIssued(serviceHub, inputRequestIssuanceState.state.data.titleID) != 0) {
            // Reject the request

            // add output state
            val outputRequestIssuanceState = requestIssuanceState.copy(status = RequestIssuanceStatus.TITLE_ALREADY_ISSUED)
            tx.addOutputState(outputRequestIssuanceState, RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID)

            // add commands
            val issuanceFailedCommand = Command(RequestIssuanceContract.Commands.Reject(), listOf(ourIdentity.owningKey))
            tx.addCommand(issuanceFailedCommand)

            // STEP: 4.
            progressTracker.currentStep = VERIFYING_TRANSACTION
            tx.verify(serviceHub)

            // STEP: 5.
            progressTracker.currentStep = SIGNING_TRANSACTION
            val signedTx = serviceHub.signInitialTransaction(tx)

            // STEP: 6.
            progressTracker.currentStep = FINALISING_LAND_TITLE_ISSUE_TRANSACTION
            return subFlow(FinalityFlow(signedTx, FINALISING_LAND_TITLE_ISSUE_TRANSACTION.childProgressTracker()))

        } else {

            try {
                // add output states
                val outputRequestIssuanceState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
                val outputLandTitleState = getLandTitleData(inputRequestIssuanceState.state.data.titleID, outputRequestIssuanceState)
                tx.addOutputState(outputRequestIssuanceState, RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID)
                tx.addOutputState(outputLandTitleState, LandTitleContract.LAND_TITLE_CONTRACT_ID)

                // add commands
                val issueCommand = Command(LandTitleContract.Commands.IssueLandTitle(), listOf(ourIdentity.owningKey))
                val approveCommand = Command(RequestIssuanceContract.Commands.ApproveRequest(), listOf(ourIdentity.owningKey))
                tx.addCommand(issueCommand)
                tx.addCommand(approveCommand)

                // STEP: 4.
                progressTracker.currentStep = VERIFYING_TRANSACTION
                tx.verify(serviceHub)

                // STEP: 5.
                progressTracker.currentStep = SIGNING_TRANSACTION
                val signedTx = serviceHub.signInitialTransaction(tx)

                // STEP: 6.
                progressTracker.currentStep = FINALISING_LAND_TITLE_ISSUE_TRANSACTION
                return subFlow(FinalityFlow(signedTx, FINALISING_LAND_TITLE_ISSUE_TRANSACTION.childProgressTracker()))
            } catch (ex: Exception) {
                // if there is an error fetching title details from external system, notify the user and ask to retry
                // update the request state with status marked as 'FAILED'

                // add output state
                val outputRequestIssuanceState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
                tx.addOutputState(outputRequestIssuanceState, RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID)

                // add commands
                val issuanceFailedCommand = Command(RequestIssuanceContract.Commands.IssuanceFailed(), listOf(ourIdentity.owningKey))
                tx.addCommand(issuanceFailedCommand)

                // STEP: 4.
                progressTracker.currentStep = VERIFYING_TRANSACTION
                tx.verify(serviceHub)

                // STEP: 5.
                progressTracker.currentStep = SIGNING_TRANSACTION
                val signedTx = serviceHub.signInitialTransaction(tx)

                // STEP: 6.
                progressTracker.currentStep = FINALISING_LAND_TITLE_ISSUE_TRANSACTION
                return subFlow(FinalityFlow(signedTx, FINALISING_LAND_TITLE_ISSUE_TRANSACTION.childProgressTracker()))
            }
        }
    }


    /**
     * Helper function to fetch the land title data from the title API
     */
    @Suspendable
    @Throws(Exception::class)
    private fun getLandTitleData(titleID: String, outputRequestIssuanceState: RequestIssuanceState): LandTitleState {
        val titleApiURL = readApiUrl("./configuration.properties")
        val response = get(titleApiURL + titleID, headers = mapOf("Accept" to "application/json"))
        if (response.statusCode == 200) {
            val addressJson = response.jsonObject.getJSONObject("property_address")
            val address = Address(
                    houseNumber = addressJson.getString("house_name_number"),
                    streetName = addressJson.getString("street_name"),
                    city = addressJson.getString("city"),
                    county = addressJson.getString("county"),
                    country = addressJson.getString("country"),
                    postalCode = addressJson.getString("postcode")
            )

            val ownerJson = response.jsonObject.getJSONObject("registered_proprietor")
            val ownerAddressJson = ownerJson.getJSONObject("address")
            val owner = CustomParty(
                    forename = ownerJson.getString("forename"),
                    surname = ownerJson.getString("surname"),
                    NIN = ownerJson.getString("national_insurance_number"),
                    address = Address(
                            houseNumber = ownerAddressJson.getString("house_name_number"),
                            streetName = ownerAddressJson.getString("street_name"),
                            city = ownerAddressJson.getString("city"),
                            county = ownerAddressJson.getString("county"),
                            country = ownerAddressJson.getString("country"),
                            postalCode = ownerAddressJson.getString("postcode")
                    ),
                    userType = when (ownerJson.getString("user_type")) {
                        "individual" -> UserType.INDIVIDUAL
                        "company" -> UserType.COMPANY
                        "overseas_company" -> UserType.OVERSEAS_COMPANY
                        "ngo" -> UserType.NGO
                        else -> throw FlowException("User type invalid")
                    },
                    email = ownerJson.getString("email"),
                    phone = ownerJson.getString("phone"),
                    confirmationOfIdentity = true,
                    publicKey = outputRequestIssuanceState.seller.publicKey,
                    signature = null
            )

            val landTitleState = LandTitleState(
                titleID = titleID,
                landTitleProperties = LandTitleProperties(
                        address = address,
                        ownerConveyancer = flowSession.counterparty,
                        owner = owner,
                        buyer = null,
                        buyerConveyancer = null
                ),
                titleIssuer = ourIdentity,
                referenceDocuments = null,
                titleType = TitleType.WHOLE,
                lastSoldValue = null,
                status = Status.ISSUED
            )
            return landTitleState

        } else {
            throw Exception("Title not found")
        }
    }

    /**
     * Read the apiUrl from the configuration file which will be present in the node's current working directory, instead of hardcoding in the flow.
     * For unit testing purpose, the api url is hardcoded, since the mocked nodes would not have the configuration file.
     */
    @Suspendable
    private fun readApiUrl(path: String): String {
        val propertiesObject = Properties()
        val titleApiURL = "http://<your_ip_address_here>:8005/v1/title/"
        try {
            val reader = FileReader(path)
            propertiesObject.load(reader)
            return propertiesObject.getProperty("titleApiUrl")
        } catch(e: Exception) {
            return titleApiURL
        }
    }

    @Suspendable
    private fun checkIfTitleAlreadyIssued(serviceHub: ServiceHub, titleID: String): Int{
        val generalCriteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL)
        val partyType = LandTitleStateSchemaV1.PersistentLandTitle::titleID.equal(titleID)
        val customCriteria = QueryCriteria.VaultCustomQueryCriteria(partyType)
        val criteria = generalCriteria.and(customCriteria)
        val results = serviceHub.vaultService.queryBy<LandTitleState>(criteria).states
        return results.size
    }
}
