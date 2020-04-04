package mops.termine2.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Umfrage;
import mops.termine2.models.Umfrageuebersicht;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.UmfrageService;
import mops.termine2.services.UmfragenUebersichtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class UmfragenUebersichtController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private UmfragenUebersichtService umfragenuebersichtService;
	
	@Autowired
	private UmfrageService umfrageService;
	
	public UmfragenUebersichtController(MeterRegistry registry) {
		authenticatedAccess = registry.counter(Konstanten.ACCESS_AUTHENTICATED);
	}
	
	@GetMapping("/umfragen")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String index(Principal principal, Model model,
		@RequestParam(name = "gruppe", defaultValue = "-1") String gruppeId) {
		
		// Account
		Account account = authenticationService.pruefeEingeloggt(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}
		
		if (gruppeService.pruefeGruppenzugriffVerweigert(account, gruppeId)) {
			throw new AccessDeniedException(Konstanten.ERROR_GROUP_ACCESS_DENIED);
		}
		
		List<Gruppe> gruppen = gruppeService.loadByBenutzerSortiert(account);		
		
		Gruppe selGruppe = gruppeService.loadByGruppeIdOderStandard(gruppeId);
		
		List<Umfrage> umfrageOffen = 
			umfragenuebersichtService.loadOffeneUmfragen(account, selGruppe);
		List<Umfrage> umfrageAbgeschlossen = 
			umfragenuebersichtService.loadAbgeschlosseneUmfragen(account, selGruppe);
		
		HashMap<String, String> groups = gruppeService.extrahiereIdUndNameAusGruppen(gruppen);
		umfrageService.setzeGruppenName(umfrageOffen, groups);
		umfrageService.setzeGruppenName(umfrageAbgeschlossen, groups);
		
		Umfrageuebersicht umfrage = new Umfrageuebersicht(umfrageAbgeschlossen,
			umfrageOffen, gruppen, selGruppe);
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_UMFRAGEN, umfrage);
		
		return "umfragen";
	}
	
	@PostMapping(path = "umfragen", params = "details")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String details(Principal principal, Model model, final HttpServletRequest request) {
		String link = "";
		if (principal != null) {
			link = request.getParameter("details");
		}
		return "redirect:/termine2/umfragen/" + link;
	}
	
}
