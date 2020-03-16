package mops.termine2;


import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.authentication.Account;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.Terminuebersicht;
import mops.termine2.models.Umfrage;
import mops.termine2.models.Umfrageuebersicht;

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
		//Dummy Daten damit man am thymeleaf arbeiten kann:
		List<String> gruppen = new ArrayList<String>();
		gruppen.add("gruppe1");
		gruppen.add("gruppe2");
		gruppen.add("gruppe3");
		
		List<Terminfindung> terminfindungenTeilgenommen = new ArrayList<Terminfindung>();
		Terminfindung termin1 = new Terminfindung();
		termin1.setErsteller("studentin");
		termin1.setBeschreibung("Dies ist eine Beschreibung für Termin 1");
		termin1.setTitel("Terminfindung 1");
		termin1.setOrt("Raum 25.12.03.35");
		termin1.setErgebnis(LocalDateTime.now());
		
		Terminfindung termin3 = new Terminfindung();
		termin3.setErsteller("studentin");
		termin3.setBeschreibung("Dies ist eine Beschreibung für Termin 3");
		termin3.setTitel("Terminfindung 1");
		termin3.setOrt("Raum 25.12.03.35");
		termin3.setErgebnis(LocalDateTime.now());
		
		terminfindungenTeilgenommen.add(termin1);
		terminfindungenTeilgenommen.add(termin3);
		
		List<Terminfindung> terminfindungenOffen = new ArrayList<Terminfindung>();
		Terminfindung termin2 = new Terminfindung();
		termin2.setErsteller("studentin");
		termin2.setBeschreibung("Dies ist eine Beschreibung für Termin 2");
		termin2.setTitel("Terminfindung 2");
		termin2.setOrt("Raum 25.12.03.35");
		termin2.setFrist(LocalDateTime.now());
		
		Terminfindung termin4 = new Terminfindung();
		termin4.setErsteller("studentin");
		termin4.setBeschreibung("Dies ist eine Beschreibung für Termin 4");
		termin4.setTitel("Terminfindung 2");
		termin4.setOrt("Raum 25.12.03.35");
		termin4.setFrist(LocalDateTime.now());
		
		terminfindungenOffen.add(termin2);
		terminfindungenOffen.add(termin4);
		
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
		
		// Dummy-Daten
		List<String> gruppen = new ArrayList<String>();
		gruppen.add("FIXLATER");
		gruppen.add("WEB24");
		gruppen.add("GIT-R-DONE");
		
		List<LocalDateTime> vorschlaege = new ArrayList<LocalDateTime>();
		vorschlaege.add(LocalDateTime.now());
		vorschlaege.add(LocalDateTime.now().plusDays(2).plusHours(3));
		
		m.addAttribute("gruppen", gruppen);
		m.addAttribute("zeiten", vorschlaege);
		
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
