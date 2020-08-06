package no.brreg.informasjonsforvaltning.organizationcatalogue.utils

import org.slf4j.LoggerFactory
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.Testcontainers
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

abstract class ApiTestContext {

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.data.mongodb.uri=mongodb://${MONGO_USER}:${MONGO_PASSWORD}@localhost:${mongoContainer.getMappedPort(MONGO_PORT)}/organization-catalogue?authSource=admin&authMechanism=SCRAM-SHA-1"
            ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {

        private val logger = LoggerFactory.getLogger(ApiTestContext::class.java)
        var mongoContainer: KGenericContainer

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

            try {
                val con = URL("http://localhost:5000/ping").openConnection() as HttpURLConnection
                con.connect()
                if (con.responseCode != 200) {
                    logger.debug("Ping to mock server failed")
                    stopMockServer()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                stopMockServer()
            } catch (e: InterruptedException) {
                e.printStackTrace()
                stopMockServer()
            }

        }
    }

}

// Hack needed because testcontainers use of generics confuses Kotlin
class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
