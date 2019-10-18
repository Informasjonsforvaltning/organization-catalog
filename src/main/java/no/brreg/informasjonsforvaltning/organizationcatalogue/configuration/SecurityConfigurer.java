package no.brreg.informasjonsforvaltning.organizationcatalogue.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableResourceServer
public class SecurityConfigurer extends ResourceServerConfigurerAdapter {

    private ResourceServerProperties resourceServerProperties;

    @Autowired
    public SecurityConfigurer(ResourceServerProperties resourceServerProperties) {
        this.resourceServerProperties = resourceServerProperties;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(resourceServerProperties.getResourceId());
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {

        http.httpBasic().disable();

        http.csrf().disable();

        http.anonymous();

        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.cors().configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.applyPermitDefaultValues();
            config.addAllowedMethod(HttpMethod.PATCH);
            config.addAllowedMethod(HttpMethod.DELETE);
            return config;
        });

        http.authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .antMatchers(HttpMethod.GET).permitAll()
            .anyRequest().authenticated();
    }

}
