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
    municipalityNumber = "1813"
    industryCode = "84.110"
    sectorCode = "6100"
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
    municipalityNumber = "5001"
    industryCode = "84.130"
    sectorCode = "6500"
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
    municipalityNumber = "0301"
    industryCode = "84.220"
    sectorCode = "6100"
    prefLabel = PrefLabel().apply {
        en = "Forsvaret"
    }
}

val NOT_UPDATED_0 =  Organization().apply {
    id = "5d5531e45c40450006848170"
    name = "Not updated name"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/44332211"
    organizationId = "44332211"
    orgType = "FYLK"
    orgPath = "/FYLKE/972417823/44332211"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/972417823"
    municipalityNumber = "0301"
    industryCode = "84.220"
    sectorCode = "6100"
    prefLabel = PrefLabel().apply {
        nb = "nbNotUpdated"
        nn = "nnNotUpdated"
        en = "enNotUpdated"
    }
}

val NOT_UPDATED_1 =  Organization().apply {
    id = "5d5531e45c40450006848169"
    name = "Organization Name"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/11223344"
    organizationId = "11223344"
    orgType = "STAT"
    orgPath = "/STAT/972417823/11223344"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/972417823"
    municipalityNumber = "0301"
    industryCode = "84.220"
    sectorCode = "6100"
}

val UPDATED_0 =  Organization().apply {
    id = "5d5531e45c40450006848170"
    name = "updatedName"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/44332211"
    organizationId = "44332211"
    orgType = "FYLK"
    orgPath = "/FYLKE/972417823/44332211"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/972417823"
    municipalityNumber = "0301"
    industryCode = "84.220"
    sectorCode = "6100"
    prefLabel = PrefLabel().apply {
        nb = "nbNotUpdated"
        nn = "nnNotUpdated"
        en = "enNotUpdated"
    }
}

val UPDATED_1 =  Organization().apply {
    id = "5d5531e45c40450006848169"
    name = "Organization Name"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/55667788"
    organizationId = "55667788"
    orgType = "ORGL"
    orgPath = "/STAT/986105174/55667788"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/986105174"
    issued = LocalDate.of(2001, 1, 3)
    municipalityNumber = "6548"
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
    municipalityNumber = "1813"
    industryCode = "84.110"
    sectorCode = "6100"
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
    municipalityNumber = "5001"
    industryCode = "84.130"
    sectorCode = "6500"
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
    municipalityNumber = "0301"
    industryCode = "84.220"
    sectorCode = "6100"
    prefLabel = PrefLabel().apply {
        en = "Forsvaret"
    }
}

val ORG_DB_3 =  OrganizationDB().apply {
    id = ObjectId("5d5531e45c40450006848169")
    name = "To Be Updated"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/11223344"
    organizationId = "11223344"
    orgType = "STAT"
    orgPath = "/STAT/972417823/11223344"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/972417823"
    municipalityNumber = "0301"
    industryCode = "84.220"
    sectorCode = "6100"
}

val ORG_DB_4 =  OrganizationDB().apply {
    id = ObjectId("5d5531e45c40450006848170")
    name = "Not updated name"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/44332211"
    organizationId = "44332211"
    orgType = "FYLK"
    orgPath = "/FYLKE/972417823/44332211"
    subOrganizationOf = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/972417823"
    municipalityNumber = "0301"
    industryCode = "84.220"
    sectorCode = "6100"
    prefLabel = PrefLabel().apply {
        nb = "nbNotUpdated"
        nn = "nnNotUpdated"
        en = "enNotUpdated"
    }
}
