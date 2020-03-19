package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.controller.formular.AntwortForm;
import mops.termine2.controller.formular.ErgebnisForm;
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
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@GetMapping("/{link}")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineDetails(Principal p, Model m, @PathVariable("link") String link) {
		Account account;
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
		} else {
			System.out.println("403");
			return "error/403";
		}
		
		Terminfindung terminfindung = terminfindungService.loadByLinkMitTerminen(link);
		if (terminfindung == null) {
			System.out.println("404");
			return "error/404";
		}
		
		if (terminfindung.getGruppe() != null
				&& !gruppeService.accountInGruppe(account, terminfindung.getGruppe())) {
			System.out.println("403");
			return "error/403";
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (terminfindung.getFrist().isBefore(now)) {
			System.out.println("ErgebnisMussAngezeigtWeren");
			return "redirect:/termine2/" + link + "/ergebnis";
		}
		
		//Wenn ergebnis Erst nach Frist angezeigt werden soll,
		// muss dies hier noch abgefragt werden und evtl auf die
		//Abstimmungsseite umgeleitet werden;
		
		Boolean bereitsTeilgenommen = terminAntwortService.hatNutzerAbgestimmt(account.getName(), link);
		if (bereitsTeilgenommen) {
			System.out.println("ergebnis");
			return "redirect:/termine2/" + link + "/ergebnis";
		} else {
			System.out.println("abstimmung");
			return "redirect:/termine2/" + link + "/abstimmung";
		}
		
	}
	
	@GetMapping("/{link}/abstimmung")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineAbstimmung(Principal p, Model m, @PathVariable("link") String link) {
		
		Account account;
		Terminfindung terminfindung = terminfindungService.loadByLinkMitTerminen(link);
		
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
		} else {
			System.out.println("404");
			return "error/403";
		}
		
		if (terminfindung == null) {
			System.out.println("404");
			return "error/404";
		}
		
		if (terminfindung.getGruppe() != null
				&& !gruppeService.accountInGruppe(account, terminfindung.getGruppe())) {
			System.out.println("403");
			return "error/403";
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (terminfindung.getFrist().isBefore(now)) {
			System.out.println("ErgebnisMussAngezeigtWeren");
			return "redirect:/termine2/" + link + "/ergebnis";
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
	
	@GetMapping("/{link}/ergebnis")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineErgebnis(Principal p, Model m, @PathVariable("link") String link) {
		
		Terminfindung terminfindung = terminfindungService.loadByLinkMitTerminen(link);
		Account account;
		List<TerminfindungAntwort> antworten;
		
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
		} else {
			System.out.println("404");
			return "error/403";
		}
		
		if (terminfindung == null) {
			System.out.println("404");
			return "error/404";
		}
		
		if (terminfindung.getGruppe() != null
				&& !gruppeService.accountInGruppe(account, terminfindung.getGruppe())) {
			System.out.println("403");
			return "error/403";
		}
		
		//Wenn ergebnis Erst nach Frist angezeigt werden soll,
		// muss dies hier noch abgefragt werden und evtl auf die
		//Abstimmungsseite umgeleitet werden;
		
		Boolean bereitsTeilgenommen = terminAntwortService.hatNutzerAbgestimmt(account.getName(), link);
		if (!bereitsTeilgenommen) {
			System.out.println("abstimmung");
			return "redirect:/termine2/" + link + "/abstimmung";
		}
		
		antworten = terminAntwortService.loadAllByLink(link);
		ErgebnisForm ergebnis = new ErgebnisForm(antworten, terminfindung);
		m.addAttribute("terminfindung", terminfindung);
		m.addAttribute("ergebnis", ergebnis);
		
		authenticatedAccess.increment();
		
		return "termine-ergebnis";
	}
	
	
	@Transactional
	@PostMapping(path = "/{link}", params = "sichern")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String saveAbstimmung(Principal p,
								 Model m,
								 @PathVariable("link") String link,
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
			return "error/404";
		}
		
		if (terminfindung.getGruppe() != null
				&& !gruppeService.accountInGruppe(account, terminfindung.getGruppe())) {
			System.out.println("403");
			return "error/403";
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (terminfindung.getFrist().isBefore(now)) {
			System.out.println("ErgebnisMussAngezeigtWeren");
			return "redirect:/termine2/" + link + "termine-abstimmung";
		}
		
		LinkWrapper linkWrapper = new LinkWrapper(link);
		if (!terminfindung.equals(letzteTerminfindung.get(linkWrapper))) {
			System.out.println("Abstimmung wurde geupdated");
			return "/termine-abstimmung?link=" + link;
		}
		
		TerminfindungAntwort terminfindungAntwort = AntwortForm.mergeToAnswer(terminfindung, account.getName(),
				antwortForm);
		
		System.out.println("jetzt wird abgestimmt");
		terminAntwortService.abstimmen(terminfindungAntwort, terminfindung);
		authenticatedAccess.increment();
		
		
		return "redirect:/termine2/" + link;
	}
}
