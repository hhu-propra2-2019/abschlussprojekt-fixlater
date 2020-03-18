package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.controller.formular.AntwortForm;
import mops.termine2.enums.Antwort;
import mops.termine2.models.LinkWrapper;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.TerminAntwortService;
import mops.termine2.services.TerminfindungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

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
	
	private HashMap<LinkWrapper, Terminfindung> letzteTerminfindung = new HashMap<>();
	
	public TermineAbstimmungController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/termine-abstimmung")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineAbstimmung(Principal p, Model m, String link) {
		System.out.println("get: " + link);
		Account account;
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
		} else {
			System.out.println("nicht autorisiert");
			return null;
		}
		
		Terminfindung terminfindung = terminfindungService.loadByLinkMitTerminen(link);
		if (terminfindung == null) {
			System.out.println("404");
			return "404";
		}
		
		if (terminfindung.getGruppe() != null
				&& !gruppeService.accountInGruppe(account, terminfindung.getGruppe())) {
			System.out.println("403");
			return "403";
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (terminfindung.getFrist().isBefore(now)) {
			System.out.println("ErgebnisMussAngezeigtWeren");
			return "termine-abstimmung";
		}
		
		TerminfindungAntwort antwort = terminAntwortService.loadByBenutzerAndLink(account.getName(), link);
		AntwortForm antwortForm = new AntwortForm();
		antwortForm.init(antwort);
		
		LinkWrapper setLink = new LinkWrapper(link);
		letzteTerminfindung.put(setLink, terminfindung);
		m.addAttribute("terminfindung", terminfindung);
		m.addAttribute("antwort", antwortForm);
		
		authenticatedAccess.increment();
		
		return "termine-abstimmung";
	}
	
	
	@Transactional
	@PostMapping("/termine-abstimmung/save")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String saveAbstimmung(Principal p,
								 Model m,
								 String link,
								 @ModelAttribute AntwortForm antwortForm) {
		Account account;
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
		} else {
			System.out.println("nicht autorisiert");
			return null;
		}
		
		Terminfindung terminfindung = terminfindungService.loadByLinkMitTerminen(link);
		if (terminfindung == null) {
			System.out.println("404");
			return "404";
		}
		
		if (terminfindung.getGruppe() != null
				&& !gruppeService.accountInGruppe(account, terminfindung.getGruppe())) {
			System.out.println("403");
			return "403";
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (terminfindung.getFrist().isBefore(now)) {
			System.out.println("AbstimmungsfristAbgelaufen");
			return "/termine-abstimmung?link=" + link;
		}
		
		LinkWrapper linkWrapper = new LinkWrapper(link);
		if (!terminfindung.equals(letzteTerminfindung.get(linkWrapper))) {
			System.out.println("Abstimmung wurde geupdated");
			return "/termine-abstimmung?link=" + link;
		}
		
		TerminfindungAntwort terminfindungAntwort = mergeToAnswer(terminfindung, account.getName(),
				antwortForm);
		
		System.out.println("jetzt wird abgestimmt");
		terminAntwortService.abstimmen(terminfindungAntwort, terminfindung);
		System.out.println("mist");
		authenticatedAccess.increment();
		
		
		return "redirect:?link=" + link;
	}
	
	private TerminfindungAntwort mergeToAnswer(Terminfindung terminfindung,
											   String benutzer,
											   AntwortForm antwortForm) {
		TerminfindungAntwort terminfindungAntwort = new TerminfindungAntwort();
		terminfindungAntwort.setKuerzel(benutzer);
		terminfindungAntwort.setLink(terminfindung.getLink());
		terminfindungAntwort.setPseudonym(antwortForm.getPseudonym());
		terminfindungAntwort.setTeilgenommen(true);
		terminfindungAntwort.setGruppe(terminfindung.getGruppe());
		
		List<LocalDateTime> termine = sortTermine(terminfindung);
		List<Antwort> antworten = antwortForm.getAntworten();
		if (termine.size() != antworten.size()) {
			System.out.println("SCHEIßE");
			System.out.println(termine);
			System.out.println(antworten);
			return null;
		}
		HashMap<LocalDateTime, Antwort> antwortenMap = new HashMap<>();
		for (int i = 0; i < termine.size(); i++) {
			antwortenMap.put(termine.get(i), antworten.get(i));
		}
		
		terminfindungAntwort.setAntworten(antwortenMap);
		return terminfindungAntwort;
	}
	
	private List<LocalDateTime> sortTermine(Terminfindung terminfindung) {
		List<LocalDateTime> termine = terminfindung.getVorschlaege();
		
		for (int i = 0; i < termine.size(); i++) {
			for (int j = i; j < termine.size(); j++) {
				if (termine.get(i).isAfter(termine.get(j))) {
					LocalDateTime tmpTermin = termine.get(j);
					termine.set(j, termine.get(i));
					termine.set(i, tmpTermin);
				}
			}
		}
		return termine;
	}
}
