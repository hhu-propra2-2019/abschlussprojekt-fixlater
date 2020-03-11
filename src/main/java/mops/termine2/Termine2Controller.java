package mops.termine2;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.authentication.Account;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.Terminuebersicht;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	
	@GetMapping("")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	public String index(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		List<String> gruppen = new ArrayList<String>();
		gruppen.add("gruppe1");
		gruppen.add("gruppe2");
		gruppen.add("gruppe3");
		
		List<Terminfindung> terminfindungenTeilgenommen = new ArrayList<Terminfindung>();
		Terminfindung termin1 = new Terminfindung();
		termin1.setErsteller("studentin");
		termin1.setBeschreibung("Dies ist eine Beschreibung für Termin1");
		termin1.setTitel("Terminfindung 1");
		termin1.setOrt("Raum 25.12.03.35");
		
		terminfindungenTeilgenommen.add(termin1);
		
		List<Terminfindung> terminfindungenOffen = new ArrayList<Terminfindung>();
		Terminfindung termin2 = new Terminfindung();
		termin2.setErsteller("studentin");
		termin2.setBeschreibung("Dies ist eine Beschreibung für Termin2");
		termin2.setTitel("Terminfindung 2");
		termin2.setOrt("Raum 25.12.03.35");
		
		terminfindungenTeilgenommen.add(termin2);
		
		Terminuebersicht termine = new Terminuebersicht(terminfindungenTeilgenommen, terminfindungenOffen,
			gruppen);
		
		m.addAttribute("termine", termine);
		
		return "termine";
	}
	
	@GetMapping("/termine-abstimmung")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	public String termineAbstimmung(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "termine-abstimmung";
	}
	
	@GetMapping("/termine-neu")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	public String termineNeu(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "termine-neu";
	}
	
	@GetMapping("/umfragen")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	public String umfragen(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "umfragen";
	}
	
	@GetMapping("/umfragen-abstimmung")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	public String umfragenAbstimmung(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "umfragen-abstimmung";
	}
	
	@GetMapping("/umfragen-neu")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	public String umfragenNeu(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(ACCOUNT, createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "umfragen-neu";
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
	
	
}
