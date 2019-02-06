package com.hmlr.states

import com.hmlr.model.Charge
import com.hmlr.model.DTCConsentStatus
import com.hmlr.model.Restriction
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

/**
 * Proposed Charge Or Restriction state to represent the new set of Charges or Restrictions that will be added for a title. For ex: Charge Restriction : B-register
 */
data class ProposedChargesAndRestrictionsState(val titleID: String,
                                               val ownerConveyancer: Party,
                                               val buyerConveyancer: Party?,
                                               val buyerLender: Party?,
                                               val restrictions: Set<Restriction>,
                                               val charges: Set<Charge>,
                                               val dischargeConsented: Boolean,
                                               val addNewChargeConsented: Boolean,
                                               val status: DTCConsentStatus,
                                               override val linearId: UniqueIdentifier = UniqueIdentifier(),
                                               override val participants: List<AbstractParty> = listOf()) : LinearState

