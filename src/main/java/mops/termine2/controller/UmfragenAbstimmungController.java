package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.controller.formular.AntwortFormUmfragen;
import mops.termine2.controller.formular.ErgebnisFormUmfragen;
import mops.termine2.models.Kommentar;
import mops.termine2.models.LinkWrapper;
import mops.termine2.models.Umfrage;
import mops.termine2.models.UmfrageAntwort;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.KommentarService;
import mops.termine2.services.UmfrageAntwortService;
import mops.termine2.services.UmfrageService;
import mops.termine2.util.LocalDateTimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
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
public class UmfragenAbstimmungController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private KommentarService kommentarService;
	
	@Autowired
	private UmfrageAntwortService umfrageAntwortService;
	
	@Autowired
	private UmfrageService umfrageService;
	
	private HashMap<LinkWrapper, Umfrage> letzteUmfrage = new HashMap<>();
	
	public UmfragenAbstimmungController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/umfragen/{link}")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfrageDetails(Principal principal, Model model, @PathVariable("link") String link) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		model.addAttribute(Konstanten.ACCOUNT, account);
		
		Umfrage umfrage = umfrageService.loadByLinkMitVorschlaegen(link);
		if (umfrage == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (gruppeService.checkGroupAccessDenied(account, umfrage.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		if (LocalDateTimeManager.istVergangen(umfrage.getFrist())) {
			return "redirect:/termine2/umfragen/" + link + "/ergebnis";
		}
		
		Boolean bereitsTeilgenommen = umfrageAntwortService.hatNutzerAbgestimmt(account.getName(), link);
		if (bereitsTeilgenommen) {
			return "redirect:/termine2/umfragen/" + link + "/ergebnis";
		} else {
			return "redirect:/termine2/umfragen/" + link + "/abstimmung";
		}
	}
	
	@GetMapping("/umfragen/{link}/abstimmung")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfrageAbstimmung(Principal principal, Model model, @PathVariable("link") String link) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		model.addAttribute(Konstanten.ACCOUNT, account);
		
		Umfrage umfrage = umfrageService.loadByLinkMitVorschlaegen(link);
		
		if (umfrage == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (gruppeService.checkGroupAccessDenied(account, umfrage.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		if (LocalDateTimeManager.istVergangen(umfrage.getFrist())) {
			return "redirect:/termine2/umfragen/" + link + "/ergebnis";
		}
		
		UmfrageAntwort antwort = umfrageAntwortService.loadByBenutzerAndLink(account.getName(), link);
		AntwortFormUmfragen antwortForm = new AntwortFormUmfragen();
		antwortForm.init(antwort);
		List<Kommentar> kommentare = kommentarService.loadByLink(link);
		
		LinkWrapper setLink = new LinkWrapper(link);
		letzteUmfrage.put(setLink, umfrage);
		model.addAttribute("umfrage", umfrage);
		model.addAttribute("antwort", antwortForm);
		model.addAttribute("kommentare", kommentare);
		model.addAttribute("neuerKommentar", new Kommentar());
		
		return "umfragen-abstimmung";
	}
	
	@GetMapping("/umfragen/{link}/ergebnis")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfrageErgebnis(Principal principal, Model model, @PathVariable("link") String link) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		model.addAttribute(Konstanten.ACCOUNT, account);
		
		Umfrage umfrage = umfrageService.loadByLinkMitVorschlaegen(link);
		
		if (umfrage == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (gruppeService.checkGroupAccessDenied(account, umfrage.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		Boolean bereitsTeilgenommen = umfrageAntwortService.hatNutzerAbgestimmt(account.getName(), link);
		if (!bereitsTeilgenommen && LocalDateTimeManager.istZukuenftig(umfrage.getFrist())) {
			return "redirect:/termine2/umfragen/" + link + "/abstimmung";
		}
		
		List<UmfrageAntwort> antworten = umfrageAntwortService.loadAllByLink(link);
		UmfrageAntwort nutzerAntwort = umfrageAntwortService.loadByBenutzerAndLink(
			account.getName(), link);
		ErgebnisFormUmfragen ergebnis = new ErgebnisFormUmfragen(antworten, umfrage, nutzerAntwort);
		List<Kommentar> kommentare = kommentarService.loadByLink(link);
		model.addAttribute("umfrage", umfrage);
		model.addAttribute("ergebnis", ergebnis);
		model.addAttribute("kommentare", kommentare);
		model.addAttribute("neuerKommentar", new Kommentar());
		
		return "umfragen-ergebnis";
	}
	
	
	@PostMapping(path = "/umfragen/{link}", params = "sichern")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String saveAbstimmung(Principal principal,
								 Model model,
								 @PathVariable("link") String link,
								 @ModelAttribute AntwortFormUmfragen antwortForm) {
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		model.addAttribute(Konstanten.ACCOUNT, account);
		
		Umfrage umfrage =
			umfrageService.loadByLinkMitVorschlaegen(link);
		if (umfrage == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (gruppeService.checkGroupAccessDenied(account, umfrage.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		if (LocalDateTimeManager.istVergangen(umfrage.getFrist())) {
			return "redirect:/termine2/umfragen/" + link + "/abstimmung";
		}
		
		LinkWrapper linkWrapper = new LinkWrapper(link);
		if (!umfrage.equals(letzteUmfrage.get(linkWrapper))) {
			return "redirect:/termine2/umfragen/" + link;
		}
		
		UmfrageAntwort umfrageAntwort = AntwortFormUmfragen.mergeToAnswer(umfrage, account.getName(),
			antwortForm);
		
		umfrageAntwortService.abstimmen(umfrageAntwort, umfrage);
		
		return "redirect:/termine2/umfragen/" + link;
	}
	
	@PostMapping(path = "/umfragen/{link}", params = "kommentarSichern")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String saveKommentar(Principal principal, Model model, 
		@PathVariable("link") String link, Kommentar neuerKommentar) {

		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		model.addAttribute(Konstanten.ACCOUNT, account);
		
		Umfrage umfrage = umfrageService.loadByLinkMitVorschlaegen(link);
		if (umfrage == null) {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, Konstanten.PAGE_NOT_FOUND);
		}
		
		if (gruppeService.checkGroupAccessDenied(account, umfrage.getGruppeId())) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		if (neuerKommentar.getPseudonym().equals("")) {
			neuerKommentar.setPseudonym(account.getName());
		}
		
		LocalDateTime now = LocalDateTime.now();
		neuerKommentar.setLink(link);
		neuerKommentar.setErstellungsdatum(now);
		model.addAttribute("neuerKommentar", neuerKommentar);
		kommentarService.save(neuerKommentar);
		
		return "redirect:/termine2/umfragen/" + link;
	}
}
