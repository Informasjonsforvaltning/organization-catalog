package no.brreg.organizationcatalogue

import com.google.common.collect.ImmutableMap
import no.brreg.organizationcatalogue.generated.model.Organization
import no.brreg.organizationcatalogue.generated.model.PrefLabel
import no.brreg.organizationcatalogue.model.OrganizationDB
import org.bson.types.ObjectId
import java.time.LocalDate

private const val MONGO_USER = "testuser"
private const val MONGO_PASSWORD = "testpassword"
private const val MONGO_AUTH = "?authSource=admin&authMechanism=SCRAM-SHA-1"
const val MONGO_PORT = 27017
const val DATABASE_NAME = "organizations"

val MONGO_ENV_VALUES: Map<String, String> = ImmutableMap.of(
    "MONGO_INITDB_ROOT_USERNAME", MONGO_USER,
    "MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD)

fun buildMongoURI(host: String, port: Int, withDbName: Boolean): String {
    var uri = "mongodb://$MONGO_USER:$MONGO_PASSWORD@$host:$port/"

    if (withDbName) {
        uri += DATABASE_NAME
    }

    return uri + MONGO_AUTH
}

val ORG_0 = Organization().apply {
    name = "REGISTERENHETEN I BRØNNØYSUND"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/974760673"
    organizationId = "974760673"
    orgType = "ORGL"
    orgPath = "/STAT/912660680/974760673"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/912660680"
    issued = LocalDate.of(1999, 2, 3)
    municipalityNumber = "http://www.test.no/fest/1813"
    industryCode = "http://www.ssb.no/nace/sn2007/84.110"
    sectorCode = "http://www.brreg.no/sektorkode/6100"
    prefLabel = PrefLabel().apply {
        nb = "Brønnøysundregistrene"
    }
}

val ORG_1 =  Organization().apply {
    name = "ATB AS"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/994686011"
    organizationId = "994686011"
    orgType = "AS"
    orgPath = "/PRIVAT/994686011"
    issued = LocalDate.of(1999, 2, 3)
    municipalityNumber = "http://www.test.no/fest/5001"
    industryCode = "http://www.ssb.no/nace/sn2007/84.130"
    sectorCode = "http://www.brreg.no/sektorkode/6500"
    prefLabel = PrefLabel().apply {
        nn = "AtB AS"
    }
}

val ORG_2 =  Organization().apply {
    name = "FORSVARET"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/986105174"
    organizationId = "986105174"
    orgType = "ORGL"
    orgPath = "/STAT/972417823/986105174"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/972417823"
    municipalityNumber = "http://www.test.no/fest/0301"
    industryCode = "http://www.ssb.no/nace/sn2007/84.220"
    sectorCode = "http://www.brreg.no/sektorkode/6100"
    prefLabel = PrefLabel().apply {
        en = "Forsvaret"
    }
}

val NEW_ORG_0 =  Organization().apply {
    name = "toBeUpdated0"
    uri = "uri0"
    organizationId = "orgId0"
    orgType = "orgForm0"
    orgPath = "/STAT/123/456"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/98765421"
    issued = LocalDate.of(1999, 2, 3)
    municipalityNumber = "http://www.test.no/fest/0456"
    industryCode = "industryUri0"
    sectorCode = "sectorUri0"
    prefLabel = PrefLabel().apply {
        nn = "nnLabel"
        en = "enLabel"
    }
}

val NEW_ORG_1 =  Organization().apply {
    name = "Name"
    uri = "uri1"
    organizationId = "orgId1"
    orgType = "orgForm1"
    orgPath = "/STAT/654/321"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/972417823"
    issued = LocalDate.of(1999, 3, 2)
    municipalityNumber = "http://www.test.no/fest/1289"
    industryCode = "industryUri1"
    sectorCode = "sectorUri1"
}

val UPDATE_ORG =  Organization().apply {
    uri = "uriUpdated"
    organizationId = "orgIdUpdated"
    orgType = "orgFormUpdated"
    orgPath = "/STAT/654/655"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/12345678"
    issued = LocalDate.of(2001, 1, 3)
    municipalityNumber = "http://www.test.no/fest/6548"
    industryCode = "industryUriUpdated"
    sectorCode = "sectorUriUpdated"
    prefLabel = PrefLabel().apply {
        nb = "nbLabelUpdated"
        nn = "nnLabelUpdated"
        en = "enLabelUpdated"
    }
}

var ORGS = listOf(ORG_0, ORG_1, ORG_2)
var EMPTY_LIST = emptyList<Organization>()

var ORG_DB_0 = OrganizationDB().apply {
    id = ObjectId("5d5531e55c404500068481da")
    name = "REGISTERENHETEN I BRØNNØYSUND"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/974760673"
    organizationId = "974760673"
    orgType = "ORGL"
    orgPath = "/STAT/912660680/974760673"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/912660680"
    issued = LocalDate.of(1999, 2, 3)
    municipalityNumber = "http://www.test.no/fest/1813"
    industryCode = "http://www.ssb.no/nace/sn2007/84.110"
    sectorCode = "http://www.brreg.no/sektorkode/6100"
    prefLabel = PrefLabel().apply {
        nb = "Brønnøysundregistrene"
    }
}

val ORG_DB_1 =  OrganizationDB().apply {
    id = ObjectId("5d5531e45c40450006848160")
    name = "ATB AS"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/994686011"
    organizationId = "994686011"
    orgType = "AS"
    orgPath = "/PRIVAT/994686011"
    issued = LocalDate.of(1999, 2, 3)
    municipalityNumber = "http://www.test.no/fest/5001"
    industryCode = "http://www.ssb.no/nace/sn2007/84.130"
    sectorCode = "http://www.brreg.no/sektorkode/6500"
    prefLabel = PrefLabel().apply {
        nn = "AtB AS"
    }
}

val ORG_DB_2 =  OrganizationDB().apply {
    id = ObjectId("5d5531e45c40450006848159")
    name = "FORSVARET"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/986105174"
    organizationId = "986105174"
    orgType = "ORGL"
    orgPath = "/STAT/972417823/986105174"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/972417823"
    municipalityNumber = "http://www.test.no/fest/0301"
    industryCode = "http://www.ssb.no/nace/sn2007/84.220"
    sectorCode = "http://www.brreg.no/sektorkode/6100"
    prefLabel = PrefLabel().apply {
        en = "Forsvaret"
    }
}
