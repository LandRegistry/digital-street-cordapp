package com.hmlr.common.utils

import com.hmlr.common.exception.NotaryNotFoundException
import com.hmlr.common.exception.StateNotFoundOnVaultException
import com.hmlr.model.ActionOnRestriction
import com.hmlr.model.ChargeRestriction
import com.hmlr.model.Restriction
import com.hmlr.schema.LandTitleStateSchemaV1
import com.hmlr.states.LandTitleState
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowException
import net.corda.core.identity.Party
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.QueryCriteria

interface FlowLogicCommonMethods {

    fun ServiceHub.firstNotary(): Party {
        return this.networkMapCache.notaryIdentities.firstOrNull()
                ?: throw NotaryNotFoundException("Notary not found.")
    }

    fun <T : ContractState> ServiceHub.loadState(linearId: UniqueIdentifier, clazz: Class<T>): StateAndRef<T> {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(null,
                listOf(linearId), Vault.StateStatus.UNCONSUMED, null)
        return this.vaultService.queryBy(clazz, queryCriteria).states.singleOrNull()
                ?: throw StateNotFoundOnVaultException("State with id $linearId not found.")
    }

    fun ServiceHub.getLandTitleAssetByTitleId(titleId: String): StateAndRef<LandTitleState> {
        val titleExpr = LandTitleStateSchemaV1.PersistentLandTitle::titleID.equal(titleId)
        val titleCriteria = QueryCriteria.VaultCustomQueryCriteria(titleExpr)

        return this.vaultService.queryBy<LandTitleState>(titleCriteria).states.singleOrNull()
                ?: throw FlowException("Land Title with id $titleId not found.")
    }

    fun ServiceHub.updateRestrictions(restrictions : Set<Restriction>, status: ActionOnRestriction, consent: Boolean): Set<Restriction> {
        val newRestriction: MutableSet<Restriction> = mutableSetOf<Restriction>()
        for (restriction in restrictions){
            when(restriction) {
                is ChargeRestriction -> {
                    newRestriction.add(ChargeRestriction(restrictionId = restriction.restrictionId, restrictionText = restriction.restrictionText, consentingParty = restriction.consentingParty, action = status, consentGiven = consent, charge = restriction.charge))
                }
                else -> {
                    newRestriction.add(Restriction(restrictionText = restriction.restrictionText, restrictionId = restriction.restrictionId, consentingParty = restriction.consentingParty, action = status, consentGiven = consent))
                }
            }
        }
        return newRestriction.toSet()
    }
}