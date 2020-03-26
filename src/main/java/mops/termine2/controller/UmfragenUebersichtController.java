package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Umfrage;
import mops.termine2.models.Umfrageuebersicht;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.UmfragenuebersichtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class UmfragenUebersichtController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private UmfragenuebersichtService umfragenuebersichtService;
	
	@Autowired
	private GruppeService gruppeService;
	
	public UmfragenUebersichtController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/umfragen")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String index(Principal p, Model m,
						@RequestParam(name = "gruppe",
							defaultValue = "-1") Long gruppe) {
		if (p != null) {
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			authenticatedAccess.increment();
			
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			gruppen = gruppen.stream()
				.sorted(Comparator.comparing(Gruppe::getName))
				.collect(Collectors.toList());
			
			HashMap<Long, String> groups = new HashMap<>();
			for (Gruppe group : gruppen) {
				groups.put(group.getId(), group.getName());
			}
			
			Gruppe selGruppe = gruppeService.loadByGruppeId(gruppe);
			
			if (selGruppe == null) {
				selGruppe = new Gruppe();
				selGruppe.setId(-1L);
				selGruppe.setName("Alle Gruppen");
			}
			
			List<Umfrage> umfrageOffen;
			List<Umfrage> umfrageAbgeschlossen;
			if (gruppe == -1L) {
				umfrageOffen =
					umfragenuebersichtService.loadOffeneUmfragenFuerBenutzer(account);
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
			m.addAttribute("umfragen", umfrage);
		}
		return "umfragen";
	}
	
	@PostMapping(path = "umfragen", params = "details")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String details(Principal p, Model m, final HttpServletRequest req) {
		String link = "";
		if (p != null) {
			link = req.getParameter("details");
		}
		return "redirect:/termine2/umfragen/" + link;
	}
}

