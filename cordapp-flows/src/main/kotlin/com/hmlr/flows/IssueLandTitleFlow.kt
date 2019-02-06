package com.hmlr.flows

import co.paralleluniverse.fibers.Suspendable
import com.hmlr.common.utils.FlowLogicCommonMethods
import com.hmlr.contracts.LandTitleContract
import com.hmlr.contracts.ProposedChargeAndRestrictionContract
import com.hmlr.contracts.RequestIssuanceContract
import com.hmlr.model.*
import com.hmlr.schema.LandTitleStateSchemaV1
import com.hmlr.states.LandTitleState
import com.hmlr.states.RequestIssuanceState
import com.hmlr.states.ProposedChargesAndRestrictionsState
import net.corda.core.contracts.Command
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step
import net.corda.core.utilities.unwrap
import khttp.get
import net.corda.core.contracts.Amount
import net.corda.core.identity.CordaX500Name
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.QueryCriteria
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.FileReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

/**
 * This is a sub-flow which will be executed on HMLR node and it will issue a Land Title on the Corda ledger.
 * The flow makes an API call to the mocked registry to fetch the title data.
 * If the API call timeout, then the exception is handled to evolve the request state accordingly.
 */
@InitiatedBy(RequestIssuanceFlow::class)
class IssueLandTitleFlow(val flowSession: FlowSession) : FlowLogic<SignedTransaction>(), FlowLogicCommonMethods {

    companion object {

        object RECEIVE_REQUEST_TRANSACTION : Step("Receive Issuance Request Transaction ID from the requesting party")
        object WAIT_REQUEST_TRANSACTION_TO_COMMIT : Step("Wait for transaction to get commit")
        object GENERATING_LAND_TITLE_ISSUE_TRANSACTION : Step("Generate Land Title Issuance transaction")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with node private key")

        object FINALISING_LAND_TITLE_ISSUE_TRANSACTION : Step("Obtaining notary signature and recording transaction") {
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
        val notary = serviceHub.firstNotary()

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
            var outputLandTitleState: LandTitleState
            val outputProposedChargesAndRestrictionsState: ProposedChargesAndRestrictionsState
            var outputRequestIssuanceState: RequestIssuanceState = requestIssuanceState.copy(status = RequestIssuanceStatus.APPROVED)
            // add output states
            try {
                outputLandTitleState = getLandTitleData(inputRequestIssuanceState.state.data.titleID, outputRequestIssuanceState)
                outputProposedChargesAndRestrictionsState = ProposedChargesAndRestrictionsState(titleID = inputRequestIssuanceState.state.data.titleID, ownerConveyancer = outputLandTitleState.landTitleProperties.ownerConveyancer, buyerConveyancer = null, restrictions = outputLandTitleState.restrictions, charges = outputLandTitleState.charges, dischargeConsented = false, addNewChargeConsented = false, status = DTCConsentStatus.ISSUED, participants = outputLandTitleState.participants)
                outputLandTitleState = outputLandTitleState.copy(proposedChargeOrRestrictionLinearId = outputProposedChargesAndRestrictionsState.linearId.toString())
            } catch (ex: Exception) {
                // if there is an error fetching title details from external system, notify the user and ask to retry
                // update the request state with status marked as 'FAILED'

                // add output state
                outputRequestIssuanceState = requestIssuanceState.copy(status = RequestIssuanceStatus.FAILED)
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
            tx.addOutputState(outputRequestIssuanceState, RequestIssuanceContract.REQUEST_ISSUANCE_CONTRACT_ID)
            tx.addOutputState(outputLandTitleState, LandTitleContract.LAND_TITLE_CONTRACT_ID)
            tx.addOutputState(outputProposedChargesAndRestrictionsState, ProposedChargeAndRestrictionContract.PROPOSED_CHARGE_RESTRICTION_CONTRACT_ID)

            // add commands
            val issueCommand = Command(LandTitleContract.Commands.IssueLandTitle(), listOf(ourIdentity.owningKey))
            val approveCommand = Command(RequestIssuanceContract.Commands.ApproveRequest(), listOf(ourIdentity.owningKey))
            val issueChargeRestriction = Command(ProposedChargeAndRestrictionContract.Commands.IssueProposedChargeAndRestriction(), listOf(ourIdentity.owningKey))

            tx.addCommand(issueCommand)
            tx.addCommand(approveCommand)
            tx.addCommand(issueChargeRestriction)

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


    /**
     * Helper function to fetch the land title data from the title API
     */
    @Suspendable
    private fun getLandTitleData(titleID: String, outputRequestIssuanceState: RequestIssuanceState): LandTitleState {
        val titleJson = queryTitleApi(titleID)
        try {
            val addressJson = titleJson.getJSONObject("address")
            val address = Address(
                    city = addressJson.getString("town_city"),
                    country = addressJson.getString("country"),
                    streetName = addressJson.getString("street"),
                    county = addressJson.getString("county"),
                    postalCode = addressJson.getString("postcode"),
                    houseNumber = addressJson.getString("house_name_number")
            )
            val ownerJson = titleJson.getJSONObject("owner")
            val ownerAddressJson = ownerJson.getJSONObject("address")
            val ownerLender = titleJson.getJSONArray("restriction_consenting_parties")[0] as JSONObject
            val ownerLenderParty = serviceHub.networkMapCache.getPeerByLegalName(CordaX500Name(organisation = ownerLender.getString("organisation"), locality = ownerLender.getString("locality"), country = ownerLender.getString("country")))!!
            val charges = titleJson.getJSONArray("charges")
            val chargesSet: MutableSet<Charge> = mutableSetOf()
            charges.forEach {
                chargesSet.add(deserializeCharges(it as JSONObject))
            }
            val restrictions = titleJson.getJSONArray("restrictions")
            val restrictionsSet: MutableSet<Restriction> = mutableSetOf()
            restrictions.forEach {
                val restriction = it as JSONObject
                val consentingPartyobject = restriction.getJSONObject("consenting_party")
                val consentingParty = serviceHub.networkMapCache.getPeerByLegalName(CordaX500Name(organisation = consentingPartyobject.getString("organisation"), locality = consentingPartyobject.getString("locality"), country = consentingPartyobject.getString("country")))!!
                if (restriction.get("charge") != JSONObject.NULL) {
                    val chargeRestrictionData = ChargeRestriction(restrictionId = restriction.getString("restriction_id"), restrictionText = restriction.getString("restriction_text"), charge = deserializeCharges(restriction.getJSONObject("charge")), consentGiven = false, action = ActionOnRestriction.NO_ACTION, consentingParty = consentingParty)
                    restrictionsSet.add(chargeRestrictionData)
                } else {
                    val res = Restriction(restrictionId = restriction.getString("restriction_id"), restrictionText = restriction.getString("restriction_text"), action = ActionOnRestriction.NO_ACTION, consentGiven = false, consentingParty = consentingParty)
                    restrictionsSet.add(res)
                }
            }

            val owner = CustomParty(
                    forename = ownerJson.getString("first_name"),
                    surname = ownerJson.getString("last_name"),
                    userID = ownerJson.getInt("identity").toString(),
                    address = Address(
                            houseNumber = ownerAddressJson.getString("house_name_number"),
                            streetName = ownerAddressJson.getString("street"),
                            city = ownerAddressJson.getString("town_city"),
                            county = ownerAddressJson.getString("county"),
                            country = ownerAddressJson.getString("country"),
                            postalCode = ownerAddressJson.getString("postcode")
                    ),
                    userType = when (ownerJson.getString("type")) {
                        "individual" -> UserType.INDIVIDUAL
                        "company" -> UserType.COMPANY
                        "overseas_company" -> UserType.OVERSEAS_COMPANY
                        "ngo" -> UserType.NGO
                        else -> throw FlowException("User type invalid")
                    },
                    email = ownerJson.getString("email_address"),
                    phone = ownerJson.getString("phone_number"),
                    confirmationOfIdentity = true,
                    publicKey = outputRequestIssuanceState.seller.publicKey,
                    signature = null
            )
            val landTitleState = LandTitleState(
                    titleID = titleID,
                    landTitleProperties = LandTitleProperties(
                            address = address,
                            ownerConveyancer = flowSession.counterparty,
                            ownerLender = ownerLenderParty,
                            owner = owner

                    ),
                    titleIssuer = ourIdentity,
                    titleType = TitleType.WHOLE,
                    lastSoldValue = null,
                    status = LandTitleStatus.ISSUED,
                    charges = chargesSet,
                    restrictions = restrictionsSet,
                    proposedChargeOrRestrictionLinearId = "ERROR IF FOUND"
            )
            logger.info("Land Title State (${landTitleState.titleID}) successfully created from register data.")
            return landTitleState
        } catch (ex: Exception) {
            logger.error("ERROR: Unable to convert title api response to LandTitleState")
            throw Exception("Unable to convert title api response to LandTitleState")
        }
    }

    @Suspendable
    private fun queryTitleApi(titleID: String): JSONObject {
        val titleApiURL = try {
            readApiUrl("./configuration.properties")
        } catch (e: FileNotFoundException) {
            logger.error("ERROR: There was an reading the title api url from file")
            throw Exception("There was an reading the title api url from file")
        }

        try {
            val response = get(titleApiURL + titleID, headers = mapOf("Accept" to "application/json"))

            when {
                response.statusCode == 200 -> return response.jsonObject
                response.statusCode == 404 -> throw Exception("Title not found")
                else -> throw Exception("Title api responded with ${response.statusCode}")
            }
        } catch(e: Exception) {
            logger.error("ERROR: There was an error contacting the title api, or the title api returned malformed data. ${e.message}")
            throw Exception("There was an error contacting the title api, or the title api returned malformed data. ${e.message}")
        }
    }

    /**
     * Read the apiUrl from the configuration file which will be present in the node's current working directory, instead of hardcoding in the flow.
     * For unit testing purpose, the api url is hardcoded, since the mocked nodes would not have the configuration file.
     */
    @Suspendable
    private fun readApiUrl(path: String): String {
        val propertiesObject = Properties()
        try {
            val reader = FileReader(path)
            propertiesObject.load(reader)
            return propertiesObject.getProperty("titleApiUrl")
        } catch (e: Exception) {
            throw FileNotFoundException()
        }
    }

    /**
     * Helper function to check if title is already issued or not
     */
    @Suspendable
    private fun checkIfTitleAlreadyIssued(serviceHub: ServiceHub, titleID: String): Int {
        val generalCriteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL)
        val partyType = LandTitleStateSchemaV1.PersistentLandTitle::titleID.equal(titleID)
        val customCriteria = QueryCriteria.VaultCustomQueryCriteria(partyType)
        val criteria = generalCriteria.and(customCriteria)
        val results = serviceHub.vaultService.queryBy<LandTitleState>(criteria).states
        return results.size
    }

    @Suspendable
    private fun deserializeCharges(chargeJson: JSONObject): Charge {
        val date = LocalDateTime.parse(chargeJson.getString("date"))
        val quantity = BigDecimal.valueOf(chargeJson.getDouble("amount"))
        val lenderJson = chargeJson.getJSONObject("lender")
        val lender = serviceHub.networkMapCache.getPeerByLegalName(CordaX500Name(organisation = lenderJson.getString("organisation"), locality = lenderJson.getString("locality"), country = lenderJson.getString("country")))!!
        return Charge(date = date.toInstant(ZoneOffset.UTC), amount = Amount.fromDecimal(quantity, Currency.getInstance(chargeJson.getString("amount_currency_code")), RoundingMode.FLOOR), lender = lender)
    }
}
