package mops.termine2;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.Terminuebersicht;
import mops.termine2.services.GruppeService;
import mops.termine2.services.TerminfindunguebersichtService;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private TerminfindunguebersichtService terminfindunguebersichtService;
	
	@Autowired
	private GruppeService gruppeService;
	
	public Termine2Controller(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("")
	@RolesAllowed({ROLE_ORGA, ROLE_STUDENTIN})
	public String index(Principal p, Model m) {
		if (p != null) {
			Account account = createAccountFromPrincipal(p);
			m.addAttribute(ACCOUNT, account);
			
			authenticatedAccess.increment();
			
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			List<String> gruppenNamen = new ArrayList<>();
			for (Gruppe g : gruppen) {
				gruppenNamen.add(g.getName());
			}
			
			List<Terminfindung> terminfindungenOffen =
				terminfindunguebersichtService.loadOffeneTerminfindungenFuerBenutzer(account);
			List<Terminfindung> terminfindungenAbgeschlossen =
				terminfindunguebersichtService.loadAbgeschlosseneTerminfindungenFuerBenutzer(account);
			
			Terminuebersicht termine = new Terminuebersicht(terminfindungenAbgeschlossen,
				terminfindungenOffen, gruppenNamen);
			
			m.addAttribute("termine", termine);
		}
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
