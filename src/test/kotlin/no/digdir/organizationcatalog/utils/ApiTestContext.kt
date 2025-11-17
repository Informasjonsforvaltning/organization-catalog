package no.digdir.organizationcatalog.utils

import org.slf4j.LoggerFactory
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.Testcontainers
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI

abstract class ApiTestContext {
    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues
                .of(
                    "spring.data.mongodb.port=${mongoContainer.getMappedPort(MONGO_PORT)}",
                ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ApiTestContext::class.java)
        var mongoContainer: KGenericContainer

        init {

            startMockServer()

            Testcontainers.exposeHostPorts(LOCAL_SERVER_PORT)

            mongoContainer =
                KGenericContainer("mongo:4.4.17")
                    .withEnv(MONGO_ENV_VALUES)
                    .withExposedPorts(MONGO_PORT)
                    .withNetworkAliases("mongodb")
                    .waitingFor(Wait.forListeningPort())
            mongoContainer.start()

            resetDB()

            try {
                val con = URI("http://localhost:5050/ping").toURL().openConnection() as HttpURLConnection
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
class KGenericContainer(
    imageName: String,
) : GenericContainer<KGenericContainer>(imageName)
