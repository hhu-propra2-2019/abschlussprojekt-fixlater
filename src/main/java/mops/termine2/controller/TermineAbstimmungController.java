package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.controller.formular.AntwortForm;
import mops.termine2.models.LinkWrapper;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.TerminAntwortService;
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
import java.util.HashMap;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class TermineAbstimmungController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private TerminfindungService terminfindungService;
	
	@Autowired
	private TerminAntwortService terminAntwortService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private GruppeService gruppeService;
	
	private HashMap<LinkWrapper, Terminfindung> letzteTerminfindung = new HashMap<LinkWrapper, Terminfindung>();
	
	public TermineAbstimmungController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/termine-abstimmung")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineAbstimmung(Principal p, Model m, String link) {
		Account account;
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
		} else {
			return null;
		}
		
		Terminfindung terminfindung = terminfindungService.loadByLinkMitTerminen(link);
		if (terminfindung == null) {
			return "404";
		}
		
		if (terminfindung.getGruppe() != null
				&& !gruppeService.accountInGruppe(account, terminfindung.getGruppe())) {
			return "403";
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (terminfindung.getFrist().isBefore(now)) {
			return "termine-abstimmung";
		}
		
		TerminfindungAntwort antwort = terminAntwortService.loadByBenutzerAndLink(account.getName(), link);
		
		LinkWrapper setLink = new LinkWrapper(link);
		letzteTerminfindung.put(setLink, terminfindung);
		m.addAttribute("terminfindung", terminfindung);
		m.addAttribute("antwort", new AntwortForm(antwort));
		
		authenticatedAccess.increment();
		
		return "termine-abstimmung";
	}
	
	@PostMapping("/termine-abstimmung/save")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String saveAbstimmung(Principal p, Model m, String link) {
		Account account;
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
		} else {
			return null;
		}
		
		authenticatedAccess.increment();
		
		return "termine-abstimmung";
	}
	
}
