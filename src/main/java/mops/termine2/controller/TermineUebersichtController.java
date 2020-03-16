package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.Terminuebersicht;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.TerminfindunguebersichtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class TermineUebersichtController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private TerminfindunguebersichtService terminfindunguebersichtService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	public TermineUebersichtController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String index(Principal p, Model m) {
		if (p != null) {
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
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
}
