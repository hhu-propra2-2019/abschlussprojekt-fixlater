package mops.termine2;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.RolesAllowed;

@Controller
@RequestMapping("/termine2")
public class Termine2Controller {
	
	private final Counter authenticatedAccess;
	
	public Termine2Controller(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("")
	@RolesAllowed({"ROLE_orga", "ROLE_studentin"})
	String index(KeycloakAuthenticationToken token, Model m) {
		authenticatedAccess.increment();
		return "termine";
	}
	
	@GetMapping("/termine-abstimmung")
	@RolesAllowed({"ROLE_orga", "ROLE_studentin"})
	String termineAbstimmung(Model m) {
		return "termine-abstimmung";
	}
	
	@GetMapping("/termine-neu")
	@RolesAllowed({"ROLE_orga", "ROLE_studentin"})
	String termineNeu(Model m) {
		return "termine-neu";
	}
	
	@GetMapping("/umfragen")
	@RolesAllowed({"ROLE_orga", "ROLE_studentin"})
	String umfragen(Model m) {
		return "umfragen";
	}
	
	@GetMapping("/umfragen-abstimmung")
	@RolesAllowed({"ROLE_orga", "ROLE_studentin"})
	String umfragenAbstimmung(Model m) {
		return "umfragen-abstimmung";
	}
	
	@GetMapping("/umfragen-neu")
	@RolesAllowed({"ROLE_orga", "ROLE_studentin"})
	String umfragenNeu(Model m) {
		return "umfragen-neu";
	}
	
}
