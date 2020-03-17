package mops.termine2.controller;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

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
	public String umfragen(Principal p, Model m) {
		if (p != null) {
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			authenticatedAccess.increment();
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			List<String> gruppenNamen = new ArrayList<>();
			for (Gruppe g : gruppen) {
				gruppenNamen.add(g.getName());
			}
			List<Umfrage> umfragenOffen = umfragenuebersichtService.loadOffeneUmfragenFuerBenutzer(account);
			List<Umfrage> umfragenAbgeschlossen =
					umfragenuebersichtService.loadAbgeschlosseneUmfragenFuerBenutzer(account);
			Umfrageuebersicht umfragen =
					new Umfrageuebersicht(umfragenAbgeschlossen, umfragenOffen, gruppenNamen);
			m.addAttribute("umfragen", umfragen);
		}
		//_______________________________
		//_______________________________
		/* Dummy Daten damit man am thymeleaf arbeiten kann:
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
		umfrage1.setGruppe("FIXLATER");
		
		umfragenTeilgenommen.add(umfrage1);
		
		Umfrage umfrage2 = new Umfrage();
		umfrage2.setErsteller("studentin");
		umfrage2.setTitel("Dinner");
		umfrage2.setBeschreibung("Was sollen wir später essen?");
		umfrage2.setFrist(LocalDateTime.now().plusHours(6));
		umfrage2.setUmfragenErgebnis("Kuchen");
		umfrage2.setGruppe("WEB24");
		
		umfragenTeilgenommen.add(umfrage2);
		
		List<Umfrage> umfragenOffen = new ArrayList<Umfrage>();
		
		Umfrage umfrage3 = new Umfrage();
		umfrage3.setErsteller("studentin");
		umfrage3.setTitel("Breakfast");
		umfrage3.setBeschreibung("Was sollen wir morgen früh essen?");
		umfrage3.setFrist(LocalDateTime.now().plusHours(23));
		umfrage3.setUmfragenErgebnis("Eggs and bacon");
		umfrage3.setGruppe("GIT-R-DONE");
		
		umfragenOffen.add(umfrage3);
		
		Umfrage umfrage4 = new Umfrage();
		umfrage4.setErsteller("studentin");
		umfrage4.setTitel("Lunch");
		umfrage4.setBeschreibung("Was sollen wir morgen Mittag essen?");
		umfrage4.setFrist(LocalDateTime.now().plusHours(23));
		umfrage4.setUmfragenErgebnis("Barbeque");
		umfrage4.setGruppe("FIXLATER");
		
		umfragenOffen.add(umfrage4);
		
		Umfrageuebersicht umfragen = new Umfrageuebersicht(umfragenTeilgenommen, umfragenOffen, gruppen);
		
		m.addAttribute("umfragen", umfragen);
		
		dummy code ends here*/
		
		return "umfragen";
	}
}
