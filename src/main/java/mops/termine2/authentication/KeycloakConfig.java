package mops.termine2.authentication;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WORKAROUND for https://issues.redhat.com/browse/KEYCLOAK-11282
 * Bean should move into {@link SecurityConfig} once Bug has been resolved
 */

@Configuration
public class KeycloakConfig {
	
	@Bean
	
	public KeycloakSpringBootConfigResolver /*CHECKSTYLE:OFF*/ KeycloakConfigResolver() /*CHECKSTYLE:ON*/ {
		
		return new KeycloakSpringBootConfigResolver();
	}
}
