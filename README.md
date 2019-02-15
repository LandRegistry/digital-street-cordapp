# HM Land Registry Digital Street Proof of Concept - CorDapp

This is a CorDapp for demonstrating a simple transfer of ownership on [Corda](https://www.corda.net/), including smart contracts and digital signatures.

## Directory Structure

**Directory** | **Description**
------------------ | --------------
/config | contains log4j configs
/cordapp-common | module defining common/reusable pieces of code
/cordapp-contracts-states | module defining states, contracts, data Models and ORM schemas
/cordapp-flows | module defining corda flows
/gradle | contains the gradle wrapper, which allows the use of gradle without installing it yourself and worrying about which version is required
/lib | contains the quasar jar which rewrites our CorDapp’s flows to be checkpointable
/src | contains end to end integration test of CorDapp

## Running the nodes from the terminal

### Pre-requisites

Please refer to the [Getting set up](https://docs.corda.net/getting-set-up.html) instructions.

You will also need a running instance of [Title API](https://github.com/LandRegistry/digital-street-title-api) within the [Digital Street development Environment](https://github.com/LandRegistry/digital-street-community-dev-env)

### Building the CorDapp

Note: The HMLR CorDapp depends on land title data from the external Title API service. The API URL has to be set as a gradle property (as shown in the commands below), or by modifying [gradle.properties](./gradle.properties).

* Open a terminal window in the digital-street-cordapp directory
* Build the test nodes with our CorDapp using the following command:
  * Unix/Mac OSX: `./gradlew deployNodes -PtitleApiUrl=http://localhost:8005/v1/titles/`
  * Windows: `gradlew.bat deployNodes -PtitleApiUrl=http://localhost:8005/v1/titles/`

### Running the CorDapp

Start the nodes by running the following command from the root of the digital-street-cordapp folder

* Unix/Mac OSX: `kotlin-source/build/nodes/runnodes`
* Windows: call `kotlin-source\build\nodes\runnodes.bat`

This will start 6 Corda Nodes

* `HMLR`
* `Conveyancer1`
* `Conveyancer2`
* `Lender1`
* `SettlingParty`
* `Notary`

### Running the Unit tests

Note: The flow-tests depends on land title data from the external Title API service. The API URL has to be set as a gradle property (as shown in the commands below), or by modifying [gradle.properties](./gradle.properties).

Note: [Instructions for running JUnit tests from an IDE](#junit-test-prerequisites).

* Open a terminal window in the digital-street-cordapp directory
* Run the test cases using the following command
  * Unix/Mac OSX: `./gradlew test -PtitleApiUrl=http://localhost:8005/v1/titles/`
  * Windows: `gradlew.bat test -PtitleApiUrl=http://localhost:8005/v1/titles/`

### Running the end to end Integration test

Note: The flow-tests depends on land title data from the external Title API service. The API URL has to be set as a gradle property (as shown in the commands below), or by modifying [gradle.properties](./gradle.properties).

* Open a terminal window in the digital-street-cordapp directory
* Run the integration test case using the following command
  * Unix/Mac OSX: `./gradlew clean integrationTest --info -PtitleApiUrl=http://localhost:8005/v1/titles/`
  * Windows: `gradlew.bat clean integrationTest --info -PtitleApiUrl=http://localhost:8005/v1/titles/`

### End-to-End CorDapp flow description

The integration test script covers the end to end journey of the CorDapp as described below:

#### STEP: 1

* A Seller's Conveyancer requests HMLR a particular land title which the seller owns
* This will create a transaction with RequestIssuanceState between the HMLR and the Conveyancer node

           ----------------------------
           |                          |
           |                          |
           | RequestIssuanceState     |
           |                          |
           |    - titleID             |
           |    - titleIssuer         |
           |    - seller              |
           |    - sellerConveyancer   |
           |    - status              |
           |                          |
           |                          |
           ----------------------------

#### STEP: 2

* HMLR will then automatically issue a land title on the Corda ledger by invoking a sub-flow
* This will create a transaction with new LandTitleState, ProposedChargesAndRestrictionState being issued on the HMLR, Seller's Conveyancer and Seller's Lender nodes

           ---------------------------------
           |                               |
           |                               |
           |  LandTitleState               |
           |                               |
           |    - titleID                  |
           |    - landTitleProperties      |
           |    - titleIssuer              |
           |    - titleType                |
           |    - lastSoldValue            |
           |    - status                   |
           |                               | 
           ---------------------------------
       
           ---------------------------------------
           |                                     |
           |                                     |
           |  ProposedChargesAndRestrictionsState|
           |                                     |
           |    - titleID                        |
           |    - ownerConveyancer               |
           |    - buyerConveyancer               |
           |    - restrictions                   |
           |    - charges                        |
           |    - status                         |
           |                                     |  
           |                                     |
           |                                     | 
           ---------------------------------------

#### STEP: 3

* The Seller's Conveyancer would then request seller's lender to provide consent for discharge
* This will update the ProposedChargesAndRestrictionState with status = `REQUEST_TO_ADD_CONSENT_FOR_DISCHARGE`

#### STEP: 4

* The Seller's Lender would then provide consent for discharge
* This will update the ProposedChargesAndRestrictionState with status = `CONSENT_FOR_DISCHARGE`

#### STEP: 5

* The Seller's Conveyancer would then create a Sales contract with the Buyer's Conveyancer
* This will create a transaction with new LandAgreementState, PaymentConfirmationState issued on both conveyancer's nodes and SettlingParty node

           ---------------------------------                     
           |                               |       
           |                               |
           |  LandAgreementState           |
           |                               |
           |    - titleID                  |
           |    - buyer                    |            
           |    - seller                   |                          
           |    - buyerConveyancer         |
           |    - sellerconveyacner        |
           |    - creationDate             |
           |    - completionDate           |
           |    - contractRate             |
           |    - purchasePrice            |
           |    - deposit                  |
           |    - contentsPrice            |
           |    - balance                  |
           |    - titleStateLinearId       |
           |    - specificIncumbrances     |
           |    - titleGuarantee           |
           |    - status                   |
           |                               |
           ---------------------------------
           
           ----------------------------------
           |                                |
           |                                |
           |  PaymentConfirmationState      |
           |                                |
           |    - titleID                   |
           |    - seller                    |
           |    - buyer                     |
           |    - purchasePrice             |
           |    - landAgreementStateLinearId|
           |    - settlingParty             |
           |    - buyerConveyancer          | 
           |    - status                    |
           |                                | 
           ----------------------------------


#### STEP: 6

* The Buyer's conveyancer would then provide consent to add a new charge to the land title
* This will update the ProposedChargesAndRestrictionState with new charge details and status = `CONSENT_FOR_NEW_CHARGE`

#### STEP: 7

* The Buyer's conveyancer approves the sales contract
* This will update the LandAgreementState with status = `APPROVED`

#### STEP: 8

* The Seller signs the sales contract
* A Smart Contract will verify the seller's signature
* This will update the LandAgreementState with status = `SIGNED`

#### STEP: 9

* The Settling Party would then provide confirmation of payment they received from buyer in their escrow account offline
* This will update the PaymentConfirmationState with status = `CONFIRM_PAYMENT_RECEIVED_IN_ESCROW`

#### STEP: 10

* The Buyer signs the sales contract
* A Smart Contract will verify the buyer's signature
* This will update the LandAgreementState with status = `COMPLETED`  

#### STEP: 11

* The land gets transferred to the buyer on the completion date if all the constraints in the Smart Contract are met

## JUnit test prerequisites

* Sets up the test working directory (after each clean) using the following command:
  * Unix/Mac OSX: `./gradlew copyConfig -PtitleApiUrl=http://localhost:8005/v1/titles/`
  * Windows: `gradlew.bat copyConfig -PtitleApiUrl=http://localhost:8005/v1/titles/`
* Add `-javaagent:../../lib/quasar.jar` as a VM option.
* Set the working directory to `build/tests`.