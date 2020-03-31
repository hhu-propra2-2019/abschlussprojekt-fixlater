package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.Terminuebersicht;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.TerminfindunguebersichtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class TermineUebersichtController {
	
	public final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private TerminfindunguebersichtService terminfindunguebersichtService;
	
	public TermineUebersichtController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String index(Principal principal, Model model,
						@RequestParam(name = "gruppe",
							defaultValue = "-1") String gruppeId) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
				
		if (gruppeService.checkGroupAccessDenied(account, gruppeId)) {
			throw new AccessDeniedException(Konstanten.GROUP_ACCESS_DENIED);
		}
		
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		gruppen = gruppeService.sortGroupsByName(gruppen);
		
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
		
		List<Terminfindung> terminfindungenOffen;
		List<Terminfindung> terminfindungenAbgeschlossen;
		if (gruppeId.contentEquals("-1")) {
			terminfindungenOffen = terminfindunguebersichtService
				.loadOffeneTerminfindungenFuerBenutzer(account);
			terminfindungenAbgeschlossen = terminfindunguebersichtService
				.loadAbgeschlosseneTerminfindungenFuerBenutzer(account);
		} else {
			terminfindungenOffen = terminfindunguebersichtService
				.loadOffeneTerminfindungenFuerGruppe(account, selGruppe.getId());
			terminfindungenAbgeschlossen = terminfindunguebersichtService
				.loadAbgeschlosseneTerminfindungenFuerGruppe(account, selGruppe.getId());
		}
		for (Terminfindung terminfindung : terminfindungenOffen) {
			terminfindung.setGruppeName(groups.get(terminfindung.getGruppeId()));
		}
		for (Terminfindung terminfindung : terminfindungenAbgeschlossen) {
			terminfindung.setGruppeName(groups.get(terminfindung.getGruppeId()));
		}
		Terminuebersicht termine = new Terminuebersicht(terminfindungenAbgeschlossen,
			terminfindungenOffen, gruppen, selGruppe);
		
		model.addAttribute(Konstanten.ACCOUNT, account);
		model.addAttribute("termine", termine);
		
		return "termine";
	}
}
