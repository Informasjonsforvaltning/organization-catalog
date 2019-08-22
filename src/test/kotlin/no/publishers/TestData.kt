package no.publishers

import com.google.common.collect.ImmutableMap
import no.publishers.generated.model.PrefLabel
import no.publishers.generated.model.Publisher
import no.publishers.model.PublisherDB
import org.bson.types.ObjectId
import java.time.LocalDate

private const val MONGO_USER = "testuser"
private const val MONGO_PASSWORD = "testpassword"
private const val MONGO_AUTH = "?authSource=admin&authMechanism=SCRAM-SHA-1"
const val MONGO_PORT = 27017
const val DATABASE_NAME = "publisherAPI"

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

val PUBLISHER_0 = Publisher().apply {
    id = "5d5531e55c404500068481da"
    name = "REGISTERENHETEN I BRØNNØYSUND"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/974760673"
    organizationId = "974760673"
    orgType = "ORGL"
    orgPath = "/STAT/912660680/974760673"
    subOrganizationOf = "http://data.brreg.no/enhetsregisteret/enhet/912660680"
    issued = LocalDate.of(1999, 2, 3)
    uriMunicipalityNumber = "http://www.test.no/fest/1813"
    uriIndustryCode = "http://www.ssb.no/nace/sn2007/84.110"
    uriSectorCode = "http://www.brreg.no/sektorkode/6100"
    prefLabel = PrefLabel().apply {
        nb = "Brønnøysundregistrene"
    }
}

val PUBLISHER_1 =  Publisher().apply {
    id = "5d5531e45c40450006848160"
    name = "ATB AS"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/994686011"
    organizationId = "994686011"
    orgType = "AS"
    orgPath = "/PRIVAT/994686011"
    issued = LocalDate.of(1999, 2, 3)
    uriMunicipalityNumber = "http://www.test.no/fest/5001"
    uriIndustryCode = "http://www.ssb.no/nace/sn2007/84.130"
    uriSectorCode = "http://www.brreg.no/sektorkode/6500"
    prefLabel = PrefLabel().apply {
        nn = "AtB AS"
    }
}

val PUBLISHER_2 =  Publisher().apply {
    id = "5d5531e45c40450006848159"
    name = "FORSVARET"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/986105174"
    organizationId = "986105174"
    orgType = "ORGL"
    orgPath = "/STAT/972417823/986105174"
    subOrganizationOf = "http://data.brreg.no/enhetsregisteret/enhet/972417823"
    uriMunicipalityNumber = "http://www.test.no/fest/0301"
    uriIndustryCode = "http://www.ssb.no/nace/sn2007/84.220"
    uriSectorCode = "http://www.brreg.no/sektorkode/6100"
    prefLabel = PrefLabel().apply {
        en = "Forsvaret"
    }
}

val NEW_PUBLISHER_0 =  Publisher().apply {
    name = "toBeUpdated0"
    uri = "uri0"
    organizationId = "orgId0"
    orgType = "orgForm0"
    orgPath = "/STAT/123/456"
    subOrganizationOf = "http://data.brreg.no/enhetsregisteret/enhet/98765421"
    issued = LocalDate.of(1999, 2, 3)
    uriMunicipalityNumber = "http://www.test.no/fest/0456"
    uriIndustryCode = "industryUri0"
    uriSectorCode = "sectorUri0"
    prefLabel = PrefLabel().apply {
        nn = "nnLabel"
        en = "enLabel"
    }
}

val NEW_PUBLISHER_1 =  Publisher().apply {
    name = "Name"
    uri = "uri1"
    organizationId = "orgId1"
    orgType = "orgForm1"
    orgPath = "/STAT/654/321"
    subOrganizationOf = "http://data.brreg.no/enhetsregisteret/enhet/972417823"
    issued = LocalDate.of(1999, 3, 2)
    uriMunicipalityNumber = "http://www.test.no/fest/1289"
    uriIndustryCode = "industryUri1"
    uriSectorCode = "sectorUri1"
}

val UPDATE_PUBLISHER =  Publisher().apply {
    uri = "uriUpdated"
    organizationId = "orgIdUpdated"
    orgType = "orgFormUpdated"
    orgPath = "/STAT/654/655"
    subOrganizationOf = "http://data.brreg.no/enhetsregisteret/enhet/12345678"
    issued = LocalDate.of(2001, 1, 3)
    uriMunicipalityNumber = "http://www.test.no/fest/6548"
    uriIndustryCode = "industryUriUpdated"
    uriSectorCode = "sectorUriUpdated"
    prefLabel = PrefLabel().apply {
        nb = "nbLabelUpdated"
        nn = "nnLabelUpdated"
        en = "enLabelUpdated"
    }
}

var PUBLISHERS = listOf(PUBLISHER_0, PUBLISHER_1, PUBLISHER_2)
var EMPTY_PUBLISHERS = emptyList<Publisher>()

var PUBLISHER_DB_0 = PublisherDB().apply {
    id = ObjectId("5d5531e55c404500068481da")
    name = "REGISTERENHETEN I BRØNNØYSUND"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/974760673"
    organizationId = "974760673"
    orgType = "ORGL"
    orgPath = "/STAT/912660680/974760673"
    subOrganizationOf = "http://data.brreg.no/enhetsregisteret/enhet/912660680"
    issued = LocalDate.of(1999, 2, 3)
    uriMunicipalityNumber = "http://www.test.no/fest/1813"
    uriIndustryCode = "http://www.ssb.no/nace/sn2007/84.110"
    uriSectorCode = "http://www.brreg.no/sektorkode/6100"
    prefLabel = PrefLabel().apply {
        nb = "Brønnøysundregistrene"
    }
}

val PUBLISHER_DB_1 =  PublisherDB().apply {
    id = ObjectId("5d5531e45c40450006848160")
    name = "ATB AS"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/994686011"
    organizationId = "994686011"
    orgType = "AS"
    orgPath = "/PRIVAT/994686011"
    issued = LocalDate.of(1999, 2, 3)
    uriMunicipalityNumber = "http://www.test.no/fest/5001"
    uriIndustryCode = "http://www.ssb.no/nace/sn2007/84.130"
    uriSectorCode = "http://www.brreg.no/sektorkode/6500"
    prefLabel = PrefLabel().apply {
        nn = "AtB AS"
    }
}

val PUBLISHER_DB_2 =  PublisherDB().apply {
    id = ObjectId("5d5531e45c40450006848159")
    name = "FORSVARET"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/986105174"
    organizationId = "986105174"
    orgType = "ORGL"
    orgPath = "/STAT/972417823/986105174"
    subOrganizationOf = "http://data.brreg.no/enhetsregisteret/enhet/972417823"
    uriMunicipalityNumber = "http://www.test.no/fest/0301"
    uriIndustryCode = "http://www.ssb.no/nace/sn2007/84.220"
    uriSectorCode = "http://www.brreg.no/sektorkode/6100"
    prefLabel = PrefLabel().apply {
        en = "Forsvaret"
    }
}
