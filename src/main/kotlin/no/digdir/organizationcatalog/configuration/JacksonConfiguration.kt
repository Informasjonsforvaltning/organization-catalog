package no.digdir.organizationcatalog.configuration

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.DeserializationFeature

@Configuration
class JacksonConfiguration {
    @Bean
    fun disableFailOnNullForPrimitives(): JsonMapperBuilderCustomizer =
        JsonMapperBuilderCustomizer { builder ->
            builder.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        }
}
