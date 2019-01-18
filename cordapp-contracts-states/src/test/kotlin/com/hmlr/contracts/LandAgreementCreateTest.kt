package com.hmlr.contracts

import com.hmlr.*
import com.hmlr.model.*
import com.hmlr.states.LandAgreementState
import com.hmlr.states.LandTitleState
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.utilities.seconds
import net.corda.finance.POUNDS
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test
import net.corda.testing.contracts.DummyState
import java.time.LocalDate

class LandAgreementCreateTest {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun mustIncludeCreateLandAgreementCommand() {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementCreateTest.DummyCommand())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyOneInputState() {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, agreementState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyTwoOutputStates() {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun mustBeSignedBySellerConveyancer () {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(ALICE.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(ALICE.publicKey, BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun sellerMustBeOwner () {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(seller = other))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun sellerConveyancerMustBeOwnerConveyancer () {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(sellerConveyancer = ALICE.party))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun creationDateCannotBeInThePast() {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(creationDate = LocalDate.now().minusDays(14)))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun completionDateCannotBeInThePast() {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(completionDate = LocalDate.now().minusDays(14)))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun completionDateMustBeAfterCreationDate() {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(creationDate = LocalDate.now().plusDays(1), completionDate = LocalDate.now()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun titleStateLinearIdMustNotChange() {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(titleStateLinearId = agreementState.linearId.toString()))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun titleIdMustNotChange() {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(titleID = "56789"))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun depositMustBeLessThanPurchasePrice() {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(deposit = 1500.POUNDS))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun balanceMustEqualPurchasePriceMinusDeposit() {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(balance = 1000.POUNDS))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }


    @Test
    fun sellerAndBuyerMustBeDifferent () {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(buyer = seller))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun sellerConveyancerAndBuyerConveyancerMustBeParticipants () {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(participants = agreementState.participants + ALICE.party))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(participants = listOf(BOB.party)))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }

    @Test
    fun agreementStatusMustBeCreated () {
        ledgerServices.ledger {
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.fails()
            }
            transaction {
                input(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleState)
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState)
                output(LandTitleContract.LAND_TITLE_CONTRACT_ID, landTitleStateWithBuyerConveyancer)
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.CreateDraftAgreement())
                command(listOf(BOB.publicKey), LandTitleContract.Commands.AssignBuyerConveyancer())
                timeWindow(ledgerServices.clock.instant(), 60.seconds)
                this.verifies()
            }
        }
    }
}