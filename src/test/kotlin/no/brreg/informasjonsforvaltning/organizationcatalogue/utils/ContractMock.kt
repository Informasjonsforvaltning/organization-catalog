package no.brreg.informasjonsforvaltning.organizationcatalogue.utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.EnhetsregisteretOrganization
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.jwk.JwkStore
import java.io.File

private val mockserver = WireMockServer(LOCAL_SERVER_PORT)

fun startMockServer() {
    if(!mockserver.isRunning) {
        mockserver.stubFor(get(urlEqualTo("/ping"))
                .willReturn(aResponse()
                        .withStatus(200))
        )

        mockserver.stubFor(get(urlEqualTo("/auth/realms/fdk/protocol/openid-connect/certs"))
            .willReturn(okJson(JwkStore.get())))

        mockserver.stubFor(get(urlEqualTo("/enhetsregisteret/api/enheter/123Null"))
                .willReturn(notFound()))

        mockserver.stubFor(get(urlEqualTo("/enhetsregisteret/api/enheter/123456789"))
            .willReturn(ok(File("src/test/resources/responses/download_123456789.json").readText())))

        mockserver.stubFor(get(urlEqualTo("/enhetsregisteret/api/enheter/987654321"))
            .willReturn(ok(File("src/test/resources/responses/download_987654321.json").readText())))

        mockserver.start()
    }
}

fun stopMockServer() {

    if (mockserver.isRunning) mockserver.stop()

}
