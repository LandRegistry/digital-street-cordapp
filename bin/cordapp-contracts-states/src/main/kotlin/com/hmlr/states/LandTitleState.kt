package com.hmlr.states

import com.hmlr.model.Attachments
import com.hmlr.model.LandTitleProperties
import com.hmlr.model.Status
import com.hmlr.model.TitleType
import com.hmlr.schema.LandTitleStateSchemaV1
import net.corda.core.contracts.Amount
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.util.*

/**
 * Data class representing Land Asset on Corda ledger.
 * It represents only a working copy of the data for PoC purpose.
 */
data class LandTitleState(val titleID: String,
                          val landTitleProperties: LandTitleProperties,
                          val titleIssuer: Party,
                          val referenceDocuments: List<Attachments>?,
                          val titleType: TitleType,
                          val lastSoldValue: Amount<Currency>?,
                          val status: Status,
                          override val linearId: UniqueIdentifier = UniqueIdentifier(),
                          override val participants: List<AbstractParty> = listOf(titleIssuer, landTitleProperties.ownerConveyancer!!))
    :LinearState, QueryableState {

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
                is LandTitleStateSchemaV1 -> LandTitleStateSchemaV1.PersistentLandTitle(
                        titleID = this.titleID,
                        issuerName = this.titleIssuer.name.commonName,
                        ownerFirstName = this.landTitleProperties.owner!!.forename,
                        ownerLastName = this.landTitleProperties.owner.surname,
                        ownerNIN = this.landTitleProperties.owner.NIN,
                        houseNumber = this.landTitleProperties.address.houseNumber,
                        streetName = this.landTitleProperties.address.streetName,
                        city = this.landTitleProperties.address.city,
                        county = this.landTitleProperties.address.county,
                        country = this.landTitleProperties.address.country,
                        postalCode = this.landTitleProperties.address.postalCode,
                        linearId = this.linearId.id.toString()
                )
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(LandTitleStateSchemaV1)
}