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
import java.time.LocalDateTime;
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
	private UmfrageService umfrageService;
	
	@Autowired
	private LinkService linkService;
	
	public UmfragenNeuController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/umfragen-neu")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neueUmfrage(Principal p, Model m) {
		// Account
		Account account;
		if (p != null) {
			// Account
			account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			authenticatedAccess.increment();
		} else {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		gruppeService.sortGroupsByName(gruppen);
		m.addAttribute("gruppen", gruppen);
		Gruppe noGroup = new Gruppe();
		noGroup.setId(-1L);
		m.addAttribute("gruppeSelektiert", noGroup);
		
		Umfrage umfrage = new Umfrage();
		umfrage.setVorschlaege(new ArrayList<String>());
		umfrage.getVorschlaege().add("");
		umfrage.setFrist(LocalDateTime.now().plusWeeks(1));
		
		m.addAttribute("umfrage", umfrage);
		m.addAttribute("fehler", "");
		
		return "umfragen-neu";
	}
	
	//neuen Vorschlag hinzufügen
	@PostMapping(path = "/umfragen-neu", params = "add")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neuerVorschlag(Principal p, Model m, Umfrage umfrage, Gruppe gruppeSelektiert) {
		// Account
		Account account;
		if (p != null) {
			account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			authenticatedAccess.increment();
		} else {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		gruppeService.sortGroupsByName(gruppen);
		m.addAttribute("gruppen", gruppen);
		
		// Selektierte Gruppe
		m.addAttribute("gruppeSelektiert", gruppeSelektiert);
		
		// Vorschlag hinzufügen
		List<String> vorschlaege = umfrage.getVorschlaege();
		vorschlaege.add("");
		
		m.addAttribute("umfrage", umfrage);
		m.addAttribute("fehler", "");
		
		return "umfragen-neu";
	}
	
	//Vorschlag löschen
	@PostMapping(path = "/umfragen-neu", params = "delete")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String voorschlagLoeschen(Principal p, Model m, Umfrage umfrage, Gruppe gruppeSelektiert,
									 final HttpServletRequest request) {
		Account account;
		if (p != null) {
			// Account
			account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			authenticatedAccess.increment();
		} else {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		gruppeService.sortGroupsByName(gruppen);
		m.addAttribute("gruppen", gruppen);
		
		// Selektierte Gruppe
		m.addAttribute("gruppeSelektiert", gruppeSelektiert);
		
		// Vorschlag löschen
		umfrage.getVorschlaege().remove(Integer.parseInt(request.getParameter("delete")));
		
		m.addAttribute("umfrage", umfrage);
		m.addAttribute("fehler", "");
		
		
		return "umfragen-neu";
	}
	
	@PostMapping(path = "/umfragen-neu", params = "create")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfrageErstellen(Principal p, Model m, Umfrage umfrage,
								   Gruppe gruppeSelektiert, RedirectAttributes ra) {
		String fehler = "";
		
		// Account
		Account account;
		if (p != null) {
			// Account
			account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			authenticatedAccess.increment();
		} else {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
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
		if (gruppeSelektiert.getId() != null && gruppeSelektiert.getId() != -1) {
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
			m.addAttribute("gruppen", gruppeService.loadByBenutzer(account));
			m.addAttribute("gruppeSelektiert", gruppeSelektiert);
			m.addAttribute("umfrage", umfrage);
			m.addAttribute("fehler", fehler);
			return "umfragen-neu";
		}
		
		umfrageService.save(umfrage);
		logger.info("Benutzer '" + account.getName() + "' hat eine neue Umfrage mit link '"
			+ umfrage.getLink() + "' erstellt");
		
		ra.addFlashAttribute("erfolg", "Die Umfrage wurde gespeichert.");
		return "redirect:/termine2/umfragen";
	}
}

