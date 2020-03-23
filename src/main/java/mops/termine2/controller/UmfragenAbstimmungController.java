package mops.termine2.controller;


import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.LinkWrapper;
import mops.termine2.models.Umfrage;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.UmfrageAntwortService;
import mops.termine2.services.UmfrageService;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class UmfragenAbstimmungController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private UmfrageService umfrageService;
	
	@Autowired
	private UmfrageAntwortService umfrageAntwortService;
	
	@Autowired
	private GruppeService gruppeService;
	
	private HashMap<LinkWrapper, Umfrage> letzteUmfrage = new HashMap<>();
	
	public UmfragenAbstimmungController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/{link}")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfrageDetails(Principal p, Model m, @PathVariable("link") String link) {
		Account account;
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
		} else {
			System.out.println("403");
			return "error/403";
		}
		
		Umfrage umfrage = umfrageService.loadByLink(link);
		if (umfrage == null) {
			System.out.println("404");
			return "error/404";
		}
		
		if (umfrage.getGruppe() != null
			&& !gruppeService.accountInGruppe(account, umfrage.getGruppe())) {
			System.out.println("403");
			return "error/403";
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (umfrage.getFrist().isBefore(now)) {
			System.out.println("ergebnis");
			return "redirect:/termine2/umfragen/" + link + "/ergebnis";
		}
		
		Boolean bereitsTeilgenommen = umfrageAntwortService.hatNutzerAbgestimmt(account.getName(), link);
		if (bereitsTeilgenommen) {
			System.out.println("ergebnis");
			return "redirect:/termine2/umfragen/" + link + "/ergebnis";
		} else {
			System.out.println("abstimmung");
			return "redirect:/termine2/umfragen/" + link + "/abstimmung";
		}
	}
	
	@GetMapping("/umfragen-abstimmung")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfragenAbstimmung(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "umfragen-abstimmung";
	}
}
