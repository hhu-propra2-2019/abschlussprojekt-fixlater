package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.controller.formular.AbstimmungsInfortmationenTermineForm;
import mops.termine2.controller.formular.AntwortForm;
import mops.termine2.controller.formular.ErgebnisForm;
import mops.termine2.models.Kommentar;
import mops.termine2.models.LinkWrapper;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.KommentarService;
import mops.termine2.services.TerminAntwortService;
import mops.termine2.services.TerminfindungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.server.ResponseStatusException;

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
	
	@Autowired
	private KommentarService kommentarService;
	
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
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		Terminfindung terminfindung =
			terminfindungService.loadByLinkMitTerminenForBenutzer(link, account.getName());
		
		if (terminfindung == null) {

			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		
		if (terminfindung.getGruppeId() != null
			&& !gruppeService.accountInGruppe(account, terminfindung.getGruppeId())) {

			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (terminfindung.getFrist().isBefore(now)) {
			return "redirect:/termine2/" + link + "/ergebnis";
		}
		
		//Wenn ergebnis Erst nach Frist angezeigt werden soll,
		// muss dies hier noch abgefragt werden und evtl auf die
		//Abstimmungsseite umgeleitet werden;
		

		if (terminfindung.getTeilgenommen()) {
			return "redirect:/termine2/" + link + "/ergebnis";
		} else {
			return "redirect:/termine2/" + link + "/abstimmung";
		}
		
	}
	
	@GetMapping("/{link}/abstimmung")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineAbstimmung(Principal p, Model m, @PathVariable("link") String link) {
		
		Account account;
		Terminfindung terminfindung;
		
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
		} else {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		terminfindung = terminfindungService.loadByLinkMitTerminenForBenutzer(link, account.getName());
		
		if (terminfindung == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (terminfindung.getGruppeId() != null
			&& !gruppeService.accountInGruppe(account, terminfindung.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);

		}
		
		LocalDateTime now = LocalDateTime.now();
		if (terminfindung.getFrist().isBefore(now)) {
			return "redirect:/termine2/" + link + "/ergebnis";
		}
		
		List<Kommentar> kommentare = kommentarService.loadByLink(link);
		TerminfindungAntwort antwort = terminAntwortService.loadByBenutzerAndLink(account.getName(), link);
		AntwortForm antwortForm = new AntwortForm();
		antwortForm.init(antwort);
		
		LinkWrapper setLink = new LinkWrapper(link);
		letzteTerminfindung.put(setLink, terminfindung);
		m.addAttribute("info", new AbstimmungsInfortmationenTermineForm(terminfindung));
		m.addAttribute("terminfindung", terminfindung);
		m.addAttribute("antwort", antwortForm);
		m.addAttribute("kommentare", kommentare);
		m.addAttribute("neuerKommentar", new Kommentar());
		
		authenticatedAccess.increment();
		
		return "termine-abstimmung";
	}
	
	@GetMapping("/{link}/ergebnis")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineErgebnis(Principal p, Model m, @PathVariable("link") String link) {
		
		Account account;
		List<TerminfindungAntwort> antworten;
		Terminfindung terminfindung;
		
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
		} else {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);

		}
		
		terminfindung = terminfindungService.loadByLinkMitTerminenForBenutzer(link, account.getName());
		
		if (terminfindung == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (terminfindung.getGruppeId() != null
			&& !gruppeService.accountInGruppe(account, terminfindung.getGruppeId())) {

			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		//Wenn ergebnis Erst nach Frist angezeigt werden soll,
		// muss dies hier noch abgefragt werden und evtl auf die
		//Abstimmungsseite umgeleitet werden;
		
		LocalDateTime now = LocalDateTime.now();
		Boolean bereitsTeilgenommen = terminAntwortService.hatNutzerAbgestimmt(account.getName(), link);
		if (!bereitsTeilgenommen && terminfindung.getFrist().isAfter(now)) {
			return "redirect:/termine2/" + link + "/abstimmung";
		}
		
		if (!terminfindung.getErgebnisVorFrist() && terminfindung.getFrist().isAfter(now)) {
			return "redirect:/termine2/" + link + "/abstimmung";
		}
		
		
		List<Kommentar> kommentare = kommentarService.loadByLink(link);
		antworten = terminAntwortService.loadAllByLink(link);
		TerminfindungAntwort nutzerAntwort = terminAntwortService.loadByBenutzerAndLink(
			account.getName(), link);
		ErgebnisForm ergebnis = new ErgebnisForm(antworten, terminfindung, nutzerAntwort);
		m.addAttribute("info", new AbstimmungsInfortmationenTermineForm(terminfindung));
		m.addAttribute("terminfindung", terminfindung);
		m.addAttribute("ergebnis", ergebnis);
		m.addAttribute("kommentare", kommentare);
		m.addAttribute("neuerKommentar", new Kommentar());
		
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
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);

		}
		
		Terminfindung terminfindung =
			terminfindungService.loadByLinkMitTerminenForBenutzer(link, account.getName());
		if (terminfindung == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (terminfindung.getGruppeId() != null
			&& !gruppeService.accountInGruppe(account, terminfindung.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		if (terminfindung.getEinmaligeAbstimmung()
			&& terminAntwortService.hatNutzerAbgestimmt(account.getName(), link)) {
			throw new AccessDeniedException(Konstanten.ACCESS_DENIED);
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (terminfindung.getFrist().isBefore(now)) {
			return "redirect:/termine2/" + link + "/abstimmung";
		}
		
		LinkWrapper linkWrapper = new LinkWrapper(link);
		if (!terminfindung.equals(letzteTerminfindung.get(linkWrapper))) {
			return "redirect:/termine2/" + link;
		}
		
		TerminfindungAntwort terminfindungAntwort = AntwortForm.mergeToAnswer(terminfindung, account.getName(),
			antwortForm);
		
		terminAntwortService.abstimmen(terminfindungAntwort, terminfindung);
		authenticatedAccess.increment();
		
		return "redirect:/termine2/" + link;
	}
	
	
	@PostMapping(path = "/{link}", params = "kommentarSichern")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String saveKommentar(Principal p, Model m, @PathVariable("link") String link, Kommentar neuerKommentar) {
		Account account;
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
		} else {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		Terminfindung terminfindung =
			terminfindungService.loadByLinkMitTerminenForBenutzer(link, account.getName());
		
		if (terminfindung == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (terminfindung.getGruppeId() != null
			&& !gruppeService.accountInGruppe(account, terminfindung.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);

		}
		
		LocalDateTime now = LocalDateTime.now();
		neuerKommentar.setLink(link);
		neuerKommentar.setErstellungsdatum(now);
		m.addAttribute("neuerKommentar", neuerKommentar);
		kommentarService.save(neuerKommentar);
		authenticatedAccess.increment();
		
		return "redirect:/termine2/" + link;
	}
}
