package com.hmlr.states

import com.hmlr.model.CustomParty
import com.hmlr.model.RequestIssuanceStatus
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

/**
 * Data class to represent request for issuance state on Corda ledger.
 */
data class RequestIssuanceState(
            val titleID: String,
            val titleIssuer: Party,
            val revenueAndCustom: Party,
            val sellerConveyancer: Party,
            val seller: CustomParty,
            val status: RequestIssuanceStatus,
            override val linearId: UniqueIdentifier = UniqueIdentifier(),
            override val participants: List<AbstractParty> = listOf(titleIssuer, sellerConveyancer)): LinearState