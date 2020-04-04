package mops.termine2.services;

import mops.termine2.authentication.Account;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.Counter;
import java.security.Principal;

/**
 * Bietet Methoden im Bezug auf Login an.
 */
@Service
public class AuthenticationService {	
	
	/**
	 * Prüft, ob der Benutzer eingeloggt ist und erhöht in diesem Fall den
	 * übergebenen Counter.
	 * 
	 * @param principal  Principal Objekt, dessen Account extrahiert werden soll.
	 * @param authenticatedAccess  Counter Objekt, das bei erfolgreicher Überprüfung
	 * 		  erhöht werden soll.
	 * 
	 * @return Das Account Objekt des Nutzers bei erfolgreicher Überprüfung,
	 * 		   oder null bei erfolgloser Überprüfung
	 */
	public Account pruefeEingeloggt(Principal principal, Counter authenticatedAccess) {
		if (principal != null) {
			Account account = erstelleAccountAusPrincipal(principal);
			authenticatedAccess.increment();
			return account;
		}
		return null;
	}
	
	private Account erstelleAccountAusPrincipal(Principal principal) {
		KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) principal;
		KeycloakPrincipal<?> keycloakToken = (KeycloakPrincipal<?>) token.getPrincipal();
		return new Account(
			keycloakToken.getName(),
			keycloakToken.getKeycloakSecurityContext().getIdToken().getEmail(),
			null,
			token.getAccount().getRoles());
	}
}
