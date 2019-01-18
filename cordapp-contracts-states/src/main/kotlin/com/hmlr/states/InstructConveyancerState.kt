package com.hmlr.states

import com.hmlr.model.CustomParty
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

/**
 * Data class to represent instruct conveyancer state. We need this state for two reasons:
 * a) It allows buyer[CustomParty] or seller[CustomParty] to inform Land Registry about their conveyancer's. This is a user agnostic state
 * b) Instructed Conveyancer would then consume this and produce [RequestIssuanceState] in the output which leaves the audit trail of the request on the ledger
 *
 * This state would typically have a short life cycle
 */
data class InstructConveyancerState(val titleID: String,
                                    val caseReferenceNumber: String,
                                    val titleIssuer: Party,
                                    val conveyancer: Party,
                                    val user: CustomParty,
                                    override val participants: List<AbstractParty> = listOf<AbstractParty>(titleIssuer, conveyancer),
                                    override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState