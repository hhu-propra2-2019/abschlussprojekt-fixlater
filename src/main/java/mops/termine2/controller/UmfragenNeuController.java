package mops.termine2.controller;


import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

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

@Controller
@SessionScope
@RequestMapping("/termine2")
public class UmfragenNeuController {
	
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
		if (p != null) {
			authenticatedAccess.increment();
			
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			m.addAttribute("gruppen", gruppen);
			Gruppe keineGruppe = new Gruppe();
			keineGruppe.setId(-1L);
			m.addAttribute("gruppeSelektiert", keineGruppe);
			
			Umfrage umfrage = new Umfrage();
			umfrage.setVorschlaege(new ArrayList<String>());
			umfrage.getVorschlaege().add("");
			umfrage.setFrist(LocalDateTime.now().plusWeeks(1));
			m.addAttribute("umfrage", umfrage);
		}
		return "umfragen-neu";
	}
	
	//neuen Vorschlag hinzufügen
	@PostMapping(path = "/umfragen-neu", params = "add")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neuerVorschlag(Principal p, Model m, Umfrage umfrage, Gruppe gruppeSelektiert) {
		if (p != null) {
			authenticatedAccess.increment();
			
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			m.addAttribute("gruppen", gruppen);
			
//			List<String> vorschlaege = umfrage.getVorschlaege();
//			vorschlaege.add("");
			umfrage.getVorschlaege().add("");
			
			m.addAttribute("gruppeSelektiert", gruppeSelektiert);
			m.addAttribute("umfrage", umfrage);
		}
		return "umfragen-neu";
	}
	
	//Vorschlag löschen
	@PostMapping(path = "/umfragen-neu", params = "delete")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String voorschlagLoeschen(Principal p, Model m, Umfrage umfrage, Gruppe gruppeSelektiert,
								 final HttpServletRequest request) {
		if (p != null) {
			authenticatedAccess.increment();
			
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			m.addAttribute("gruppen", gruppen);
			
			m.addAttribute("gruppeSelektiert", gruppeSelektiert);
			
			umfrage.getVorschlaege().remove(Integer.parseInt(request.getParameter("delete")));
			
			m.addAttribute("umfrage", umfrage);
		}
		
		return "umfragen-neu";
	}
	
	@PostMapping(path = "/umfragen-neu", params = "create")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfrageErstellen(Principal p, Model m, Umfrage umfrage,
										 Gruppe gruppeSelektiert) {
		if (p != null) {
			authenticatedAccess.increment();
			
			// Account
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			for (String ldt : umfrage.getVorschlaege()) {
				if (ldt == null) {
					System.out.println("Fehler");
					// TODO: Fehlermeldung ausgeben und auf Umfrage erstellen weiterleiten
				}
			}
			
			umfrage.setErsteller(account.getName());
			umfrage.setLoeschdatum(umfrage.getFrist().plusWeeks(3));
			if (gruppeSelektiert.getId() != null && gruppeSelektiert.getId() != -1) {
				Gruppe gruppe = gruppeService.loadById(gruppeSelektiert.getId());
				umfrage.setGruppe(gruppe.getName());
			}
			
			String link = linkService.generiereEindeutigenLink();
			umfrage.setLink(link);
			
			umfrageService.save(umfrage);
		}	
		return "redirect:/termine2/umfragen";
	}
}

