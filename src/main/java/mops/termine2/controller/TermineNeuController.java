package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.LinkService;
import mops.termine2.services.TerminfindungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class TermineNeuController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private TerminfindungService terminfindungService;
	
	@Autowired
	private LinkService linkService;
	
	public TermineNeuController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/termine-neu")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineNeu(Principal p, Model m) {
		if (p != null) {
			authenticatedAccess.increment();
			
			/* Account */
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			/* Gruppen */
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			m.addAttribute("gruppen", gruppen);
			m.addAttribute("gruppeSelektiert", gruppen.get(0));
			
			Terminfindung terminfindung = new Terminfindung();
			terminfindung.setVorschlaege(new ArrayList<>());
			terminfindung.getVorschlaege().add(LocalDateTime.now());
			terminfindung.setFrist(LocalDateTime.now().plusWeeks(1));
			
			m.addAttribute("terminfindung", terminfindung);
		}
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "add")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neuenTermineHinzufügen(Principal p, Model m, Terminfindung terminfindung, Gruppe gruppeSelektiert) {
		if (p != null) {
			authenticatedAccess.increment();
			
			// Account
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			// Gruppen
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			m.addAttribute("gruppen", gruppen);
			
			// Terminvorschlag hinzufügen
			List<LocalDateTime> termine = terminfindung.getVorschlaege();
			termine.add(LocalDateTime.now());
			
			// Selektierte Gruppe
			m.addAttribute("gruppeSelektiert", gruppeSelektiert);
			
			m.addAttribute("terminfindung", terminfindung);
		}
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "create")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String terminfindungErstellen(Principal p, Model m, Terminfindung terminfindung, Gruppe gruppeSelektiert) {
		if (p != null) {
			authenticatedAccess.increment();
			
			// Account
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			// Terminfindung erstellen
			terminfindung.setErsteller(account.getName());
			terminfindung.setLoeschdatum(terminfindung.getFrist().plusWeeks(3));
			if (gruppeSelektiert.getId() != -1) {
				Gruppe gruppe = gruppeService.loadById(gruppeSelektiert.getId());
				terminfindung.setGruppe(gruppe.getName());
			}
			
			String link = linkService.generiereEindeutigenLink();
			terminfindung.setLink(link);
			
			terminfindungService.save(terminfindung);
		}
		
		return "redirect:/termine2";
	}
}
