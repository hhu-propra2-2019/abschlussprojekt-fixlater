package mops.termine2;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.authentication.Account;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class Termine2Controller {
	
	public static final String ROLE_ORGA = "ROLE_orga";
	
	public static final String ROLE_STUDENTIN = "ROLE_studentin";
	
	public static final String ACCOUNT = "account";
	
	private final transient Counter authenticatedAccess;
	
	public Termine2Controller(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	private Account createAccountFromPrincipal(Principal principal) {
		
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
	
	@GetMapping("")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	String index(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "termine";
	}
	
	@GetMapping("/termine-abstimmung")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	String termineAbstimmung(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "termine-abstimmung";
	}
	
	@GetMapping("/termine-neu")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	String termineNeu(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "termine-neu";
	}
	
	@GetMapping("/umfragen")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	String umfragen(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "umfragen";
	}
	
	@GetMapping("/umfragen-abstimmung")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	String umfragenAbstimmung(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "umfragen-abstimmung";
	}
	
	@GetMapping("/umfragen-neu")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	String umfragenNeu(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "umfragen-neu";
	}
	
}
