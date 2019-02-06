package com.hmlr.states

import com.hmlr.model.CustomParty
import com.hmlr.model.PaymentConfirmationStatus
import net.corda.core.contracts.Amount
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import java.util.*

data class PaymentConfirmationState(val titleID: String,
                                    val seller: CustomParty,
                                    val buyer: CustomParty,
                                    val purchasePrice: Amount<Currency>,
                                    val landAgreementStateLinearId: String,
                                    val status: PaymentConfirmationStatus,
                                    val settlingParty: Party,
                                    val buyerConveyancer: Party,
                                    override val linearId: UniqueIdentifier = UniqueIdentifier(),
                                    override val participants: List<AbstractParty> = listOf()): LinearState