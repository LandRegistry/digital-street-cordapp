package com.hmlr.contracts

import com.hmlr.*
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.utilities.seconds
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class LandTitleAssignBuyerConveyancerTest {

    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))
    class DummyCommand : TypeOnlyCommandData()

    @Test
    fun mustIncludeAssignBuyerConveyancerCommand() {
        val outputLandTitleState = landTitleState.copy(participants = landTitleState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(BOB.publicKey), LandTitleAssignBuyerConveyancerTest.DummyCommand())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.IssueLandTitle())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyOneOInputState() {
        val outputLandTitleState = landTitleState.copy(participants = landTitleState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeTwoOutputStates() {
        val outputLandTitleState = landTitleState.copy(participants = landTitleState.participants + CHARLIE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun buyerConveyancerMustBeParticipant() {
        val outputLandTitleState = landTitleState.copy(participants = landTitleState.participants + CHARLIE.party)
        val invalidOutputLandTitleState = landTitleState.copy(participants = landTitleState.participants + ALICE.party)
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, invalidOutputLandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun mustBeSignedByOwnersConveyancer() {
        val outputLandTitleState = landTitleState.copy(participants = landTitleState.participants + CHARLIE.party)
         ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(ALICE.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(CHARLIE.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }

            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, outputLandTitleState)
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }
}
