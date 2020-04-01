package mops.termine2.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;

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
import mops.termine2.services.TerminfindungAntwortService;
import mops.termine2.services.TerminfindungErgebnisService;
import mops.termine2.services.TerminfindungService;
import mops.termine2.util.LocalDateTimeManager;

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

@Controller
@SessionScope
@RequestMapping("/termine2")
public class TermineAbstimmungController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private KommentarService kommentarService;
	
	@Autowired
	private TerminfindungAntwortService terminAntwortService;
	
	@Autowired
	private TerminfindungService terminfindungService;
	
	@Autowired
	private TerminfindungErgebnisService ergebnisService;
	
	private HashMap<LinkWrapper, Terminfindung> letzteTerminfindung = new HashMap<>();
	
	public TermineAbstimmungController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/{link}")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineDetails(Principal principal, Model model, @PathVariable("link") String link) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		Terminfindung terminfindung = 
			terminfindungService.loadByLinkMitTerminenForBenutzer(link, account.getName());
		
		if (terminfindung == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (gruppeService.checkGroupAccessDenied(account, terminfindung.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		if (LocalDateTimeManager.istVergangen(terminfindung.getFrist())
			|| terminfindung.getTeilgenommen()) {
			return "redirect:/termine2/" + link + "/ergebnis";
		}
		return "redirect:/termine2/" + link + "/abstimmung";
	}
	
	@GetMapping("/{link}/abstimmung")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineAbstimmung(Principal principal, Model model, @PathVariable("link") String link) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		Terminfindung terminfindung = 
			terminfindungService.loadByLinkMitTerminenForBenutzer(link, account.getName());
		
		if (terminfindung == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (gruppeService.checkGroupAccessDenied(account, terminfindung.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
			
		}
		
		if (LocalDateTimeManager.istVergangen(terminfindung.getFrist())) {
			return "redirect:/termine2/" + link + "/ergebnis";
		}
		
		List<Kommentar> kommentare = kommentarService.loadByLink(link);
		TerminfindungAntwort antwort = terminAntwortService.loadByBenutzerAndLink(account.getName(), link);
		AntwortForm antwortForm = new AntwortForm();
		antwortForm.init(antwort);
		
		LinkWrapper setLink = new LinkWrapper(link);
		letzteTerminfindung.put(setLink, terminfindung);
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_INFO, new AbstimmungsInfortmationenTermineForm(terminfindung));
		model.addAttribute(Konstanten.MODEL_TERMINFINDUNG, terminfindung);
		model.addAttribute(Konstanten.MODEL_ANTWORT, antwortForm);
		model.addAttribute(Konstanten.MODEL_KOMMENTARE, kommentare);
		model.addAttribute(Konstanten.MODEL_NEUER_KOMMENTAR, new Kommentar());
		
		return "termine-abstimmung";
	}
	
	@GetMapping("/{link}/ergebnis")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineErgebnis(Principal principal, Model model, @PathVariable("link") String link) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		Terminfindung terminfindung = 
			terminfindungService.loadByLinkMitTerminenForBenutzer(link, account.getName());
		
		if (terminfindung == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (gruppeService.checkGroupAccessDenied(account, terminfindung.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		Boolean bereitsTeilgenommen = terminAntwortService.hatNutzerAbgestimmt(account.getName(), link);
		if (!bereitsTeilgenommen && LocalDateTimeManager.istZukuenftig(terminfindung.getFrist())) {
			return "redirect:/termine2/" + link + "/abstimmung";
		}
		
		if (!terminfindung.getErgebnisVorFrist()
			&& LocalDateTimeManager.istZukuenftig(terminfindung.getFrist())) {
			return "redirect:/termine2/" + link + "/abstimmung";
		}
		
		List<Kommentar> kommentare = kommentarService.loadByLink(link);
		List<TerminfindungAntwort> antworten = terminAntwortService.loadAllByLink(link);
		TerminfindungAntwort nutzerAntwort = terminAntwortService.loadByBenutzerAndLink(
			account.getName(), link);
		ErgebnisForm ergebnis = ergebnisService.baueErgebnisForm(antworten,
			terminfindung, nutzerAntwort);
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_INFO, new AbstimmungsInfortmationenTermineForm(terminfindung));
		model.addAttribute(Konstanten.MODEL_TERMINFINDUNG, terminfindung);
		model.addAttribute(Konstanten.MODEL_ERGEBNIS, ergebnis);
		model.addAttribute(Konstanten.MODEL_KOMMENTARE, kommentare);
		model.addAttribute(Konstanten.MODEL_NEUER_KOMMENTAR, new Kommentar());
		
		return "termine-ergebnis";
	}
	
	@Transactional
	@PostMapping(path = "/{link}", params = "sichern")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String saveAbstimmung(Principal principal,
		Model model,
		@PathVariable("link") String link,
		@ModelAttribute AntwortForm antwortForm) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		Terminfindung terminfindung = 
			terminfindungService.loadByLinkMitTerminenForBenutzer(link, account.getName());
		if (terminfindung == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (gruppeService.checkGroupAccessDenied(account, terminfindung.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		if (terminfindung.getEinmaligeAbstimmung()
			&& terminAntwortService.hatNutzerAbgestimmt(account.getName(), link)) {
			throw new AccessDeniedException(Konstanten.ACCESS_DENIED);
		}
		
		if (LocalDateTimeManager.istVergangen(terminfindung.getFrist())) {
			return "redirect:/termine2/" + link + "/abstimmung";
		}
		
		LinkWrapper linkWrapper = new LinkWrapper(link);
		if (!terminfindung.equals(letzteTerminfindung.get(linkWrapper))) {
			return "redirect:/termine2/" + link;
		}
		
		TerminfindungAntwort terminfindungAntwort = AntwortForm.mergeToAnswer(terminfindung,
			account.getName(), antwortForm);
		
		terminAntwortService.abstimmen(terminfindungAntwort, terminfindung);
		
		return "redirect:/termine2/" + link;
	}
	
	@PostMapping(path = "/{link}", params = "kommentarSichern")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String saveKommentar(Principal principal, Model model,
		@PathVariable("link") String link, Kommentar neuerKommentar) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		Terminfindung terminfindung = 
			terminfindungService.loadByLinkMitTerminenForBenutzer(link, account.getName());
		
		if (terminfindung == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (gruppeService.checkGroupAccessDenied(account, terminfindung.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		if (neuerKommentar.getPseudonym().equals("")) {
			neuerKommentar.setPseudonym(account.getName());
		}
		
		LocalDateTime now = LocalDateTime.now();
		neuerKommentar.setLink(link);
		neuerKommentar.setErstellungsdatum(now);
		kommentarService.save(neuerKommentar);
		
		return "redirect:/termine2/" + link;
	}
	
}
