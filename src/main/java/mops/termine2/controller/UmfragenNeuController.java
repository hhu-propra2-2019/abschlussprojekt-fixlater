package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Umfrage;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.LinkService;
import mops.termine2.services.UmfrageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/umfragen-neu")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neueUmfrage(Principal principal, Model model) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		model.addAttribute(Konstanten.ACCOUNT, account);
		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		gruppeService.sortGroupsByName(gruppen);
		model.addAttribute("gruppen", gruppen);
		Gruppe noGroup = new Gruppe();
		noGroup.setId("-1");
		model.addAttribute("gruppeSelektiert", noGroup);
		
		// Umfrage
		Umfrage umfrage = umfrageService.createDefaultUmfrage();
		
		model.addAttribute("umfrage", umfrage);
		model.addAttribute("fehler", "");
		
		return "umfragen-neu";
	}
	
	//neuen Vorschlag hinzufügen
	@PostMapping(path = "/umfragen-neu", params = "add")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neuerVorschlag(Principal principal, Model model, Umfrage umfrage, Gruppe gruppeSelektiert) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		model.addAttribute(Konstanten.ACCOUNT, account);
		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		gruppeService.sortGroupsByName(gruppen);
		model.addAttribute("gruppen", gruppen);
		
		// Selektierte Gruppe
		model.addAttribute("gruppeSelektiert", gruppeSelektiert);
		
		// Vorschlag hinzufügen
		List<String> vorschlaege = umfrage.getVorschlaege();
		vorschlaege.add("");
		
		model.addAttribute("umfrage", umfrage);
		model.addAttribute("fehler", "");
		
		return "umfragen-neu";
	}
	
	//Vorschlag löschen
	@PostMapping(path = "/umfragen-neu", params = "delete")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String voorschlagLoeschen(Principal principal, Model model, Umfrage umfrage, Gruppe gruppeSelektiert,
									 final HttpServletRequest request) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		model.addAttribute(Konstanten.ACCOUNT, account);
		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		gruppeService.sortGroupsByName(gruppen);
		model.addAttribute("gruppen", gruppen);
		
		// Selektierte Gruppe
		model.addAttribute("gruppeSelektiert", gruppeSelektiert);
		
		// Vorschlag löschen
		umfrage.getVorschlaege().remove(Integer.parseInt(request.getParameter("delete")));
		
		model.addAttribute("umfrage", umfrage);
		model.addAttribute("fehler", "");
		
		
		return "umfragen-neu";
	}
	
	@PostMapping(path = "/umfragen-neu", params = "create")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfrageErstellen(Principal principal, Model model,
								   Umfrage umfrage, Gruppe gruppeSelektiert,
								   RedirectAttributes redirectAttributes) {
		String fehler = "";
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		model.addAttribute(Konstanten.ACCOUNT, account);
		
		// Vorschläge filtern. Doppelte und nicht gesetzte Daten löschen
		ArrayList<String> gueltigeVorschlaege = new ArrayList<String>();
		for (String vorschlag : umfrage.getVorschlaege()) {
			if (vorschlag != null && !vorschlag.equals("") && !gueltigeVorschlaege.contains(vorschlag)) {
				gueltigeVorschlaege.add(vorschlag);
			}
		}
		
		if (gueltigeVorschlaege.isEmpty()) {
			gueltigeVorschlaege.add("");
			fehler = "Es muss mindestens einen Vorschlag geben.";
		}
		
		umfrage.setVorschlaege(gueltigeVorschlaege);
		umfrage.setMaxAntwortAnzahl((long) gueltigeVorschlaege.size());
		
		// Umfrage erstellen
		umfrage.setErsteller(account.getName());
		if (gruppeSelektiert.getId() != null && !gruppeSelektiert.getId().contentEquals("-1")) {
			Gruppe gruppe = gruppeService.loadByGruppeId(gruppeSelektiert.getId());
			umfrage.setGruppeId(gruppe.getId());
		}
		
		if (umfrage.getLink().isEmpty()) {
			String link = linkService.generiereEindeutigenLink();
			umfrage.setLink(link);
		} else {
			if (!linkService.pruefeEindeutigkeitLink(umfrage.getLink())) {
				fehler = "Der eingegebene Link existiert bereits.";
			}
			if (!linkService.isLinkValid(umfrage.getLink())) {
				fehler = "Der eingegebene Link enthält ungültige Zeichen.";
			}
		}
		
		if (!fehler.equals("")) {
			model.addAttribute("gruppen", gruppeService.loadByBenutzer(account));
			model.addAttribute("gruppeSelektiert", gruppeSelektiert);
			model.addAttribute("umfrage", umfrage);
			model.addAttribute("fehler", fehler);
			return "umfragen-neu";
		}
		
		umfrageService.save(umfrage);
		logger.info("Benutzer '" + account.getName() + "' hat eine neue Umfrage mit link '"
			+ umfrage.getLink() + "' erstellt");
		
		redirectAttributes.addFlashAttribute("erfolg", "Die Umfrage wurde gespeichert.");
		return "redirect:/termine2/umfragen";
	}
}

