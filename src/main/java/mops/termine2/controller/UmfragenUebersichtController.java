package mops.termine2.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.security.Principal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Umfrage;
import mops.termine2.models.Umfrageuebersicht;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.UmfragenuebersichtService;

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
	private UmfragenuebersichtService umfragenuebersichtService;
	
	public UmfragenUebersichtController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/umfragen")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String index(Principal principal, Model model,
		@RequestParam(name = "gruppe", defaultValue = "-1") String gruppeId) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		if (gruppeService.checkGroupAccessDenied(account, gruppeId)) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		gruppen = gruppen.stream()
			.sorted(Comparator.comparing(Gruppe::getName))
			.collect(Collectors.toList());
		
		HashMap<String, String> groups = new HashMap<>();
		for (Gruppe group : gruppen) {
			groups.put(group.getId(), group.getName());
		}
		
		Gruppe selGruppe = gruppeService.loadByGruppeId(gruppeId);
		
		if (selGruppe == null) {
			selGruppe = new Gruppe();
			selGruppe.setId("-1");
			selGruppe.setName("Alle Gruppen");
		}
		
		List<Umfrage> umfrageOffen;
		List<Umfrage> umfrageAbgeschlossen;
		if (gruppeId.contentEquals("-1")) {
			umfrageOffen = umfragenuebersichtService.loadOffeneUmfragenFuerBenutzer(account);
			umfrageAbgeschlossen = umfragenuebersichtService
				.loadAbgeschlosseneUmfragenFuerBenutzer(account);
		} else {
			umfrageOffen = umfragenuebersichtService
				.loadOffeneUmfragenFuerGruppe(account, selGruppe.getId());
			umfrageAbgeschlossen = umfragenuebersichtService
				.loadAbgeschlosseneUmfragenFuerGruppe(account, selGruppe.getId());
		}
		for (Umfrage umfrage : umfrageOffen) {
			umfrage.setGruppeName(groups.get(umfrage.getGruppeId()));
		}
		for (Umfrage umfrage : umfrageAbgeschlossen) {
			umfrage.setGruppeName(groups.get(umfrage.getGruppeId()));
		}
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
