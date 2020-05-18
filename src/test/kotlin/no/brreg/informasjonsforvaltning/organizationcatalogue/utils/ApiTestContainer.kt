package no.brreg.informasjonsforvaltning.organizationcatalogue.utils

import org.slf4j.LoggerFactory
import org.testcontainers.Testcontainers
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.IOException

abstract class ApiTestContainer {
    companion object {

        private val logger = LoggerFactory.getLogger(ApiTestContainer::class.java)
        var mongoContainer: KGenericContainer
        var TEST_API: KGenericContainer

        init {

            startMockServer()

            Testcontainers.exposeHostPorts(LOCAL_SERVER_PORT)
            val apiNetwork = Network.newNetwork()

            mongoContainer = KGenericContainer("mongo:latest")
                .withEnv(MONGO_ENV_VALUES)
                .withExposedPorts(MONGO_PORT)
                .withNetwork(apiNetwork)
                .withNetworkAliases("mongodb")
                .waitingFor(Wait.forListeningPort())
            mongoContainer.start()

            populateDB()

            TEST_API = KGenericContainer(System.getProperty("testImageName") ?: "eu.gcr.io/digdir-fdk-infra/organization-catalogue:latest")
                .withExposedPorts(API_PORT)
                .dependsOn(mongoContainer)
                .withEnv(API_ENV_VALUES)
                .waitingFor(Wait.forHttp("/ready").forStatusCode(200))
                .withNetwork(apiNetwork)
                .withFileSystemBind("./target/jacoco-agent", "/jacoco-agent", BindMode.READ_WRITE)
                .withFileSystemBind("./target/jacoco-report", "/jacoco-report",BindMode.READ_WRITE)
                .withCommand("java",
                    "-javaagent:/jacoco-agent/org.jacoco.agent-runtime.jar=destfile=/jacoco-report/jacoco-it.exec",
                    "-jar",
                    "/app.jar")

            TEST_API.start()


            try {
                val result = TEST_API.execInContainer("wget", "-O", "-", "$WIREMOCK_TEST_HOST/auth/realms/fdk/protocol/openid-connect/certs")
                if (!result.stderr.contains("200")) {
                    logger.debug("Ping to AuthMock server failed")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }

        fun stopGracefully() {
            logger.debug("Shutting down container gracefully")
            TEST_API.dockerClient
                .stopContainerCmd(ApiTestContainer.TEST_API.containerId)
                .withTimeout(100)
                .exec()
        }
    }

}

// Hack needed because testcontainers use of generics confuses Kotlin
class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
