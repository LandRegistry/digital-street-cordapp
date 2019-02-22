package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.*
import com.hmlr.utils.BasicSDLT
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class LandAgreementBuyerSignsTests : AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))

    @Test
    fun `must Include Buyer Sign AgreementCommand`() {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementBuyerSignsTests.DummyCommand())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Input State`() {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `input AgreementStatus Must Be Approved`() {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.CREATED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Output State`() {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By Buyer`() {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        val buyerWithWrongSignature = seller.copy(signature = sign(agreementState.titleID, wrongBuyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithWrongSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `output AgreementStatus Must Be Completed` () {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.CREATED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }
}