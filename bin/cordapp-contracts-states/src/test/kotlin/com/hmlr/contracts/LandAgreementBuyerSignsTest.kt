package com.hmlr.contracts

import com.hmlr.*
import com.hmlr.model.*
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.crypto.Crypto
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test
import java.security.PrivateKey

class LandAgreementBuyerSignsTest {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.hmlr.contracts"))
    private fun sign(message: String, key: PrivateKey): ByteArray {
        return Crypto.doSign(Crypto.RSA_SHA256, key, message.toByteArray())
    }

    @Test
    fun mustIncludeBuyerSignAgreementCommand() {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementBuyerSignsTest.DummyCommand())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyOneInputState() {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun inputAgreementStatusMustBeApproved() {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.CREATED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun mustIncludeExactlyOneOutputState() {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun mustBeSignedByBuyer() {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        val buyerWithWrongSignature = seller.copy(signature = sign(agreementState.titleID, wrongBuyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithWrongSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }

    @Test
    fun outputAgreementStatusMustBeCompleted () {
        val buyerWithSignature = buyer.copy(signature = sign(agreementState.titleID, buyerPrivateKey))
        ledgerServices.ledger {
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.CREATED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.APPROVED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.fails()
            }
            transaction {
                input(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.SIGNED))
                output(LandAgreementContract.LAND_AGREEMENT_CONTRACT_ID, agreementState.copy(status = AgreementStatus.COMPLETED, buyer = buyerWithSignature))
                command(listOf(CHARLIE.publicKey), LandAgreementContract.Commands.BuyerSignAgreement())
                this.verifies()
            }
        }
    }
}