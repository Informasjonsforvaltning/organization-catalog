package no.digdir.organizationcatalog

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableWebSecurity
@EnableScheduling
open class Application {
    @Bean
    open fun validatingMongoEventListener(): ValidatingMongoEventListener = ValidatingMongoEventListener(validator())

    @Bean
    open fun validator(): LocalValidatorFactoryBean = LocalValidatorFactoryBean()
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
