package no.digdir.organizationcatalog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import no.digdir.organizationcatalog.configuration.AppProperties
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
@EnableScheduling
open class Application {
    @Bean
    open fun validatingMongoEventListener(): ValidatingMongoEventListener {
        return ValidatingMongoEventListener(validator())
    }

    @Bean
    open fun validator(): LocalValidatorFactoryBean {
        return LocalValidatorFactoryBean()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
