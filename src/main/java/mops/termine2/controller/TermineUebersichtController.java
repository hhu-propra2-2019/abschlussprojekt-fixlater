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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
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
	public String index(Principal p, Model m,
						@RequestParam(name = "gruppe",
							defaultValue = "Alle Gruppen") String gruppe) {
		if (p != null) {
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			authenticatedAccess.increment();
			
			List<String> gruppenNamen = new ArrayList<>();
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			for (Gruppe g : gruppen) {
				gruppenNamen.add(g.getName());
			}
			
			List<Terminfindung> terminfindungenOffen;
			List<Terminfindung> terminfindungenAbgeschlossen;
			if (gruppe.equals("Alle Gruppen")) {
				terminfindungenOffen =
					terminfindunguebersichtService.loadOffeneTerminfindungenFuerBenutzer(account);
				terminfindungenAbgeschlossen = terminfindunguebersichtService
					.loadAbgeschlosseneTerminfindungenFuerBenutzer(account);
			} else {
				terminfindungenOffen =
					terminfindunguebersichtService.loadOffeneTerminfindungenFuerGruppe(gruppe);
				terminfindungenAbgeschlossen = terminfindunguebersichtService
					.loadAbgeschlosseneTerminfindungenFuerGruppe(gruppe);
			}
			Terminuebersicht termine = new Terminuebersicht(terminfindungenAbgeschlossen,
				terminfindungenOffen, gruppenNamen);
			
			m.addAttribute("termine", termine);
			m.addAttribute("selektierteGruppe", gruppe);
		}
		return "termine";
	}
	
	@PostMapping(path = "", params = "details")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String details(Principal p, Model m, final HttpServletRequest req) {
		String link = "";
		if (p != null) {
			link = req.getParameter("details");
		}
		return "redirect:/termine2/" + link;
	}
}
