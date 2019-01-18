package com.hmlr.schema

import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Persistence
import javax.persistence.Table

/**
* Family of Schema
*/
object LandTitleStateSchema

/**
* First version of the Schema class that is ORM into Corda RDMS for efficient queries
*
*/
object LandTitleStateSchemaV1: MappedSchema(
        schemaFamily = LandTitleStateSchema::class.java,
        version = 1,
        mappedTypes = listOf(PersistentLandTitle::class.java)) {

    @Entity
    @Table(name = "land_title")
    class PersistentLandTitle(
            @Column(name = "title_id")
            var titleID: String,

            @Column(name = "issuer")
            var issuerName: String?,

            @Column(name = "owner_first_name")
            var ownerFirstName: String,

            @Column(name = "owner_last_name")
            var ownerLastName: String,

            @Column(name = "owner_id")
            var ownerID: String,

            @Column(name = "houseNumber")
            var houseNumber: String,

            @Column(name = "streetName")
            var streetName: String,

            @Column(name = "city")
            var city: String,

            @Column(name = "county")
            var county: String,

            @Column(name = "country")
            var country: String,

            @Column(name = "postal_code")
            var postalCode: String,

            @Column(name = "linear_id")
            var linearId: String
    ): PersistentState(){
        constructor() : this("", "", "", "", "", "", "", "", "", "", "",
                ""
        )
    }
}