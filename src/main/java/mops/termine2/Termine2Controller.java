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
		
		//Dummy Daten damit man am thymeleaf arbeiten kann:
		List<String> gruppen = new ArrayList<String>();
		gruppen.add("FIXLATER");
		gruppen.add("WEB24");
		gruppen.add("GIT-R-DONE");
		
		List<Umfrage> umfragenTeilgenommen = new ArrayList<Umfrage>();
		
		Umfrage umfrage1 = new Umfrage();
		umfrage1.setErsteller("studentin");
		umfrage1.setTitel("Brunch");
		umfrage1.setBeschreibung("Was sollen wir essen?");
		umfrage1.setFrist(LocalDateTime.now().plusHours(3));
		umfrage1.setUmfragenErgebnis("Spaghetti");
		
		umfragenTeilgenommen.add(umfrage1);
		
		Umfrage umfrage2 = new Umfrage();
		umfrage2.setErsteller("studentin");
		umfrage2.setTitel("Dinner");
		umfrage2.setBeschreibung("Was sollen wir später essen?");
		umfrage2.setFrist(LocalDateTime.now().plusHours(6));
		umfrage2.setUmfragenErgebnis("Kuchen");
		
		umfragenTeilgenommen.add(umfrage2);
		
		List<Umfrage> umfragenOffen = new ArrayList<Umfrage>();
		
		Umfrage umfrage3 = new Umfrage();
		umfrage3.setErsteller("studentin");
		umfrage3.setTitel("Breakfast");
		umfrage3.setBeschreibung("Was sollen wir morgen früh essen?");
		umfrage3.setFrist(LocalDateTime.now().plusHours(23));
		umfrage3.setUmfragenErgebnis("Eggs and bacon");
		
		umfragenOffen.add(umfrage3);
		
		Umfrage umfrage4 = new Umfrage();
		umfrage4.setErsteller("studentin");
		umfrage4.setTitel("Lunch");
		umfrage4.setBeschreibung("Was sollen wir morgen Mittag essen?");
		umfrage4.setFrist(LocalDateTime.now().plusHours(23));
		umfrage4.setUmfragenErgebnis("Barbeque");
		
		umfragenOffen.add(umfrage4);
		
		Umfrageuebersicht umfragen = new Umfrageuebersicht(umfragenTeilgenommen, umfragenOffen, gruppen);
		
		m.addAttribute("umfragen", umfragen);
		
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
