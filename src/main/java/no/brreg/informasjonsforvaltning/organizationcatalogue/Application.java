package no.brreg.informasjonsforvaltning.organizationcatalogue;

import no.brreg.informasjonsforvaltning.organizationcatalogue.configuration.AppProperties;
import org.apache.jena.riot.RIOT;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootApplication
@EnableConfigurationProperties({ AppProperties.class })
public class Application {

    public static void main(String[] args) {
        RIOT.init();
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}
