package com.hmlr.contracts

import com.hmlr.AbstractContractsStatesTestUtils
import com.hmlr.model.*
import com.hmlr.utils.BasicSDLT
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class LandAgreementSellerSignsTests : AbstractContractsStatesTestUtils() {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))


    @Test
    fun `must Include Seller Sign Agreement Command`() {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementSellerSignsTests.DummyCommand())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Input State`() {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `input AgreementStatus Must Be Approved`() {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.CREATED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Include Exactly One Output State`() {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `must Be Signed By Seller`() {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        val sellerWithWrongSignature = seller.copy(signature = sign(agreementState.titleID, wrongSellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithWrongSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun `output Agreement Status Must Be Signed`() {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.CREATED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature, isMortgageTermsAdded = true, sdlt = BasicSDLT().computeSDLT(agreementState.purchasePrice)))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }
}