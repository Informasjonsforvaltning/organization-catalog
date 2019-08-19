package no.publishers

import com.google.common.collect.ImmutableMap
import no.publishers.generated.model.Code
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
    orgForm = "ORGL"
    orgPath = "/STAT/912660680/974760673"
    orgParent = "http://data.brreg.no/enhetsregisteret/enhet/912660680"
    municipalityNumber = "1813"
    issued = LocalDate.of(1999, 2, 3)
    industryCode = Code().apply{
        uri = "http://www.ssb.no/nace/sn2007/84.110"
        code = "84.110"
        prefLabel = PrefLabel().apply {
            nb = "Generell offentlig administrasjon"
        }
    }
    sectorCode = Code().apply{
        uri = "http://www.brreg.no/sektorkode/6100"
        code = "6100"
        prefLabel = PrefLabel().apply {
            nb = "Statsforvaltningen"
        }
    }
    prefLabel = PrefLabel().apply {
        nb = "Brønnøysundregistrene"
    }
}

val PUBLISHER_1 =  Publisher().apply {
    id = "5d5531e45c40450006848160"
    name = "ATB AS"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/994686011"
    organizationId = "994686011"
    orgForm = "AS"
    orgPath = "/PRIVAT/994686011"
    municipalityNumber = "5001"
    issued = LocalDate.of(1999, 2, 3)
    industryCode = Code().apply{
        uri = "http://www.ssb.no/nace/sn2007/84.130"
        code = "84.130"
        prefLabel = PrefLabel().apply {
            nb = "Offentlig administrasjon tilknyttet næringsvirksomhet og arbeidsmarked"
        }
    }
    sectorCode = Code().apply{
        uri = "http://www.brreg.no/sektorkode/6500"
        code = "6500"
        prefLabel = PrefLabel().apply {
            nb = "Kommuneforvaltningen"
        }
    }
    prefLabel = PrefLabel().apply {
        nb = "AtB AS"
    }
}

val PUBLISHER_2 =  Publisher().apply {
    id = "5d5531e45c40450006848159"
    name = "FORSVARET"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/986105174"
    organizationId = "986105174"
    orgForm = "ORGL"
    orgPath = "/STAT/972417823/986105174"
    orgParent = "http://data.brreg.no/enhetsregisteret/enhet/972417823"
    municipalityNumber = "0301"
    issued = LocalDate.of(1999, 2, 3)
    industryCode = Code().apply{
        uri = "http://www.ssb.no/nace/sn2007/84.220"
        code = "84.220"
        prefLabel = PrefLabel().apply {
            nb = "Forsvar"
        }
    }
    sectorCode = Code().apply{
        uri = "http://www.brreg.no/sektorkode/6100"
        code = "6100"
        prefLabel = PrefLabel().apply {
            nb = "Statsforvaltningen"
        }
    }
    prefLabel = PrefLabel().apply {
        nb = "Forsvaret"
    }
}

val PUBLISHER_3 =  Publisher().apply {
    name = "toBeUpdated"
    uri = "uri"
    organizationId = "orgId"
    orgForm = "orgForm"
    orgPath = "/STAT/123/456"
    orgParent = "http://data.brreg.no/enhetsregisteret/enhet/98765421"
    municipalityNumber = "0456"
    issued = LocalDate.of(1999, 2, 3)
    industryCode = Code().apply{
        uri = "industryUri"
        code = "industryCode"
        prefLabel = PrefLabel().apply {
            nb = "nbIndustryLabel"
            nn = "nnIndustryLabel"
            en = "enIndustryLabel"
        }
    }
    sectorCode = Code().apply{
        uri = "sectorUri"
        code = "sectorCode"
        prefLabel = PrefLabel().apply {
            nb = "nbSectorLabel"
            nn = "nnSectorLabel"
            en = "enSectorLabel"
        }
    }
    prefLabel = PrefLabel().apply {
        nb = "nbLabel"
        nn = "nnLabel"
        en = "enLabel"
    }
}

var PUBLISHERS = listOf(PUBLISHER_0, PUBLISHER_1, PUBLISHER_2)
var EMPTY_PUBLISHERS = emptyList<Publisher>()

var PUBLISHER_DB_0 = PublisherDB().apply {
    id = ObjectId("5d5531e55c404500068481da")
    name = "REGISTERENHETEN I BRØNNØYSUND"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/974760673"
    organizationId = "974760673"
    orgForm = "ORGL"
    orgPath = "/STAT/912660680/974760673"
    orgParent = "http://data.brreg.no/enhetsregisteret/enhet/912660680"
    municipalityNumber = "1813"
    issued = LocalDate.of(1999, 2, 3)
    industryCode = Code().apply{
        uri = "http://www.ssb.no/nace/sn2007/84.110"
        code = "84.110"
        prefLabel = PrefLabel().apply {
            nb = "Generell offentlig administrasjon"
        }
    }
    sectorCode = Code().apply{
        uri = "http://www.brreg.no/sektorkode/6100"
        code = "6100"
        prefLabel = PrefLabel().apply {
            nb = "Statsforvaltningen"
        }
    }
    prefLabel = PrefLabel().apply {
        nb = "Brønnøysundregistrene"
    }
}

val PUBLISHER_DB_1 =  PublisherDB().apply {
    id = ObjectId("5d5531e45c40450006848160")
    name = "ATB AS"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/994686011"
    organizationId = "994686011"
    orgForm = "AS"
    orgPath = "/PRIVAT/994686011"
    municipalityNumber = "5001"
    issued = LocalDate.of(1999, 2, 3)
    industryCode = Code().apply{
        uri = "http://www.ssb.no/nace/sn2007/84.130"
        code = "84.130"
        prefLabel = PrefLabel().apply {
            nb = "Offentlig administrasjon tilknyttet næringsvirksomhet og arbeidsmarked"
        }
    }
    sectorCode = Code().apply{
        uri = "http://www.brreg.no/sektorkode/6500"
        code = "6500"
        prefLabel = PrefLabel().apply {
            nb = "Kommuneforvaltningen"
        }
    }
    prefLabel = PrefLabel().apply {
        nb = "AtB AS"
    }
}

val PUBLISHER_DB_2 =  PublisherDB().apply {
    id = ObjectId("5d5531e45c40450006848159")
    name = "FORSVARET"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/986105174"
    organizationId = "986105174"
    orgForm = "ORGL"
    orgPath = "/STAT/972417823/986105174"
    orgParent = "http://data.brreg.no/enhetsregisteret/enhet/972417823"
    municipalityNumber = "0301"
    issued = LocalDate.of(1999, 2, 3)
    industryCode = Code().apply{
        uri = "http://www.ssb.no/nace/sn2007/84.220"
        code = "84.220"
        prefLabel = PrefLabel().apply {
            nb = "Forsvar"
        }
    }
    sectorCode = Code().apply{
        uri = "http://www.brreg.no/sektorkode/6100"
        code = "6100"
        prefLabel = PrefLabel().apply {
            nb = "Statsforvaltningen"
        }
    }
    prefLabel = PrefLabel().apply {
        nb = "Forsvaret"
    }
}
