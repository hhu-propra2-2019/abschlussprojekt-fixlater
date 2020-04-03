package mops.termine2.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Umfrage;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.LinkService;
import mops.termine2.services.UmfrageService;
import mops.termine2.util.IntegerToolkit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class UmfragenNeuController {
	
	private final Logger logger = Logger.getLogger(UmfragenNeuController.class.getName());
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private LinkService linkService;
	
	@Autowired
	private UmfrageService umfrageService;
	
	public UmfragenNeuController(MeterRegistry registry) {
		authenticatedAccess = registry.counter(Konstanten.ACCESS_AUTHENTICATED);
	}
	
	@GetMapping("/umfragen-neu")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neueUmfrage(Principal principal, Model model) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_GRUPPEN, gruppeService.loadByBenutzerSortiert(account));
		model.addAttribute(Konstanten.MODEL_GRUPPE_SELEKTIERT, gruppeService.erstelleStandardGruppe());
		model.addAttribute(Konstanten.MODEL_UMFRAGE, umfrageService.createDefaultUmfrage());
		model.addAttribute(Konstanten.MODEL_FEHLER, "");
		
		return "umfragen-neu";
	}
	
	// neuen Vorschlag hinzufügen
	@PostMapping(path = "/umfragen-neu", params = "add")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neuerVorschlag(Principal principal, Model model, Umfrage umfrage, Gruppe gruppeSelektiert) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}
		
		// Vorschlag hinzufügen
		List<String> vorschlaege = umfrage.getVorschlaege();
		vorschlaege.add("");
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_GRUPPEN, gruppeService.loadByBenutzerSortiert(account));
		model.addAttribute(Konstanten.MODEL_GRUPPE_SELEKTIERT, gruppeSelektiert);
		model.addAttribute(Konstanten.MODEL_UMFRAGE, umfrage);
		model.addAttribute(Konstanten.MODEL_FEHLER, "");
		
		return "umfragen-neu";
	}
	
	// Vorschlag löschen
	@PostMapping(path = "/umfragen-neu", params = "delete")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String voorschlagLoeschen(Principal principal, Model model, Umfrage umfrage, Gruppe gruppeSelektiert,
		final HttpServletRequest request) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}
		
		// Vorschlag löschen
		int indexToDelete = IntegerToolkit.getInt(request.getParameter("delete"));
		umfrageService.loescheVorschlag(umfrage, indexToDelete);
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_GRUPPEN, gruppeService.loadByBenutzerSortiert(account));
		model.addAttribute(Konstanten.MODEL_GRUPPE_SELEKTIERT, gruppeSelektiert);
		model.addAttribute(Konstanten.MODEL_UMFRAGE, umfrage);
		model.addAttribute(Konstanten.MODEL_FEHLER, "");
		
		return "umfragen-neu";
	}
	
	@PostMapping(path = "/umfragen-neu", params = "create")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfrageErstellen(Principal principal, Model model,
		Umfrage umfrage, Gruppe gruppeSelektiert,
		RedirectAttributes redirectAttributes) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}
		
		List<String> fehler = umfrageService.erstelleUmfrage(account,
			umfrage);
		fehler.addAll(linkService.setzeOderPruefeLink(umfrage));
		gruppeService.setzeGruppeId(umfrage, gruppeSelektiert);
		
		if (fehler.isEmpty()) {
			umfrageService.save(umfrage);
			logger.info("Benutzer '" + account.getName() + "' hat eine neue Umfrage mit Link '"
				+ umfrage.getLink() + "' erstellt");
			
			redirectAttributes.addFlashAttribute(Konstanten.MODEL_ERFOLG, 
				Konstanten.MESSAGE_UMFRAGE_GESPEICHERT);
			return "redirect:/termine2/umfragen";
		}
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_GRUPPEN, gruppeService.loadByBenutzer(account));
		model.addAttribute(Konstanten.MODEL_GRUPPE_SELEKTIERT, gruppeSelektiert);
		model.addAttribute(Konstanten.MODEL_UMFRAGE, umfrage);
		model.addAttribute(Konstanten.MODEL_FEHLER, fehler.get(fehler.size() - 1));
		
		return "umfragen-neu";
	}
	
}
