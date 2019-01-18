package com.hmlr.states

import com.hmlr.model.AgreementStatus
import com.hmlr.model.CustomParty
import com.hmlr.model.TitleGuarantee
import net.corda.core.contracts.Amount
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

data class LandAgreementState(val titleID: String,
                              val buyer: CustomParty,
                              val seller: CustomParty,
                              val buyerConveyancer: Party,
                              val sellerConveyancer: Party,
                              val creationDate: LocalDate,
                              val completionDate: LocalDate,
                              val contractRate: Double,
                              val purchasePrice: Amount<Currency>,
                              val deposit: Amount<Currency>,
                              val contentsPrice: Amount<Currency>?,
                              val balance: Amount<Currency>,
                              val titleStateLinearId: String,
                              val specificIncumbrances: List<String>?,
                              val titleGuarantee: TitleGuarantee,
                              val status: AgreementStatus,
                              override val linearId: UniqueIdentifier = UniqueIdentifier(),
                              override val participants: List<AbstractParty> = listOf(sellerConveyancer, buyerConveyancer)
                              ): LinearState {}