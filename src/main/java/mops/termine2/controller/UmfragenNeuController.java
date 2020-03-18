package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.ArrayList;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class UmfragenNeuController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	public UmfragenNeuController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/umfragen-neu")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfragenNeu(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		ArrayList<String> gruppen = new ArrayList<String>();
		//gruppen.add("FIXLATER");
		//gruppen.add("WEB24");
		//gruppen.add("GIT-R-DONE");
		
		ArrayList<String> vorschlaege = new ArrayList<String>();
		vorschlaege.add("Grillen!");
		vorschlaege.add("Hotdogs");
		vorschlaege.add("Kabelsalat aus der Entwickelbar");
		vorschlaege.add("Bananen-Milkshake");
		vorschlaege.add("Spezi");
		vorschlaege.add("Heißer Kakao mit Rum");
		
		m.addAttribute("gruppen", gruppen);
		m.addAttribute("vorschlaege", vorschlaege);
		
		return "umfragen-neu";
	}
}

