package com.hmlr.contracts

import com.hmlr.*
import com.hmlr.model.*
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.crypto.Crypto
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test
import java.security.PrivateKey

class LandAgreementSellerSignsTest {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))
    private fun sign(message: String, key: PrivateKey): ByteArray {
        return Crypto.doSign(Crypto.RSA_SHA256, key, message.toByteArray())
    }

    @Test
    fun mustIncludeSellerSignAgreementCommand() {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementSellerSignsTest.DummyCommand())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyOneInputState() {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun inputAgreementStatusMustBeApproved() {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.CREATED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyOneOutputState() {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun mustBeSignedBySeller() {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        val sellerWithWrongSignature = seller.copy(signature = sign(agreementState.titleID, wrongSellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithWrongSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun outputAgreementStatusMustBeSigned () {
        val sellerWithSignature = seller.copy(signature = sign(agreementState.titleID, sellerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.CREATED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, seller = sellerWithSignature))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, seller = sellerWithSignature ))
                command(listOf(BOB.publicKey), LandAgreementContract.Commands.SellerSignAgreement())
                this.verifies()
            }
        }
    }
}