package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Umfrage;
import mops.termine2.models.Umfrageuebersicht;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.UmfragenuebersichtService;
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
public class UmfragenUebersichtController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private UmfragenuebersichtService umfragenuebersichtService;
	
	@Autowired
	private GruppeService gruppeService;
	
	public UmfragenUebersichtController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/umfragen")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfragen(Principal p, Model m,
					@RequestParam(name = "gruppe", defaultValue = "Alle Gruppen") String gruppe) {
		if (p != null) {
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			authenticatedAccess.increment();
			
			List<String> gruppenNamen = new ArrayList<>();
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			for (Gruppe g : gruppen) {
				gruppenNamen.add(g.getName());
			}
			
			List<Umfrage> umfragenOffen;
			List<Umfrage> umfragenAbgeschlossen;
			if (gruppe.equals("Alle Gruppen")) {
				umfragenOffen =
					umfragenuebersichtService.loadOffeneUmfragenFuerBenutzer(account);
				umfragenAbgeschlossen = umfragenuebersichtService
					.loadAbgeschlosseneUmfragenFuerBenutzer(account);
			} else {
				umfragenOffen =
					umfragenuebersichtService.loadOffeneUmfragenFuerGruppe(gruppe);
				umfragenAbgeschlossen = umfragenuebersichtService
					.loadAbgeschlosseneUmfragenFuerGruppe(gruppe);
			}
			Umfrageuebersicht umfragen =
					new Umfrageuebersicht(umfragenAbgeschlossen, umfragenOffen, gruppenNamen);
			
			m.addAttribute("umfragen", umfragen);
			m.addAttribute("selektierteGruppe", gruppe);
		}
		
		return "umfragen";
	}
	
	@PostMapping(path = "umfragen", params = "details")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String details(Principal p, Model m, final HttpServletRequest req) {
		String link = "";
		if (p != null) {
			link = req.getParameter("details");
		}
		return "redirect:/termine2/umfragen" + link;
	}
}

