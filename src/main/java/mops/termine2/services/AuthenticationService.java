package mops.termine2.services;

import mops.termine2.authentication.Account;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService {
	
	/**
	 * Generiert einen Account mit einem KeyCloak Token 
	 * @param principal Ein java-security Interface welches einen Nutzer representiert
	 * @return Account
	 */
	public Account createAccountFromPrincipal(Principal principal) {
		if (principal instanceof KeycloakAuthenticationToken) {
			KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) principal;
			KeycloakPrincipal keycloakToken = (KeycloakPrincipal) token.getPrincipal();
			return new Account(
				keycloakToken.getName(),
				keycloakToken.getKeycloakSecurityContext().getIdToken().getEmail(),
				null,
				token.getAccount().getRoles());
		} else {
			Set<String> roles = new HashSet<String>();
			roles.add("studentin");
			return new Account(
				principal.getName(),
				null,
				null,
				roles);
		}
	}
	
}
