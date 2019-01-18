package com.hmlr.states

import com.hmlr.model.AgreementStatus
import com.hmlr.model.CustomParty
import com.hmlr.model.TitleGuarantee
import net.corda.core.contracts.*
import net.corda.core.flows.FlowLogicRefFactory
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import java.time.Instant
import java.time.LocalDate
import java.util.*

/**
 * Data class to represent Land Title agreement state on Corda ledger.
 * This state purely defines the relationship/contract between buying and selling party.
 * Additionally implement [SchedulableState] to trigger land title transfer transaction on [completionDate].
 */
data class LandAgreementState(val titleID: String,
                              val buyer: CustomParty,
                              val seller: CustomParty,
                              val buyerConveyancer: Party,
                              val sellerConveyancer: Party,
                              val creationDate: LocalDate,
                              val completionDate: Instant,
                              val contractRate: Double,
                              val purchasePrice: Amount<Currency>,
                              val deposit: Amount<Currency>,
                              val contentsPrice: Amount<Currency>?,
                              val balance: Amount<Currency>,
                              val titleStateLinearId: String,
                              val specificIncumbrances: List<String>?,
                              val titleGuarantee: TitleGuarantee,
                              val status: AgreementStatus,
                              val isMortgageTermsAdded: Boolean,
                              override val linearId: UniqueIdentifier = UniqueIdentifier(),
                              override val participants: List<AbstractParty> = listOf(sellerConveyancer, buyerConveyancer))
    : LinearState, SchedulableState{
    private val scheduledTime = completionDate
    override fun nextScheduledActivity(thisStateRef: StateRef, flowLogicRefFactory: FlowLogicRefFactory): ScheduledActivity? {

        /**
         * Transfer land title only when the Agreement state status is [AgreementStatus.COMPLETED]
         * i.e. after the buyer has signed on the agreement
         */
        if (status != AgreementStatus.COMPLETED) {
            return null
        } else {
            return ScheduledActivity(flowLogicRefFactory.create("com.hmlr.flows.InitiateLandTitleTransferFlow", thisStateRef), scheduledTime)
        }
    }
}
