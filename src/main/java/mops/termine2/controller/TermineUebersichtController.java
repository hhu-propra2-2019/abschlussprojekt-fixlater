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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class TermineUebersichtController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private TerminfindunguebersichtService terminfindunguebersichtService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	public TermineUebersichtController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String index(Principal p, Model m,
						@RequestParam(name = "gruppe",
							defaultValue = "-1") Long gruppe) {
		Account account;
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
			account = authenticationService.createAccountFromPrincipal(p);
			authenticatedAccess.increment();
		} else {
			return "error/403";
		}
		
		if (gruppe != -1 && !gruppeService.accountInGruppe(account, gruppe)) {
			return "error/403";
		}
		
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		
		gruppen = gruppen.stream()
			.sorted(Comparator.comparing(Gruppe::getName))
			.collect(Collectors.toList());
		
		Gruppe selGruppe = gruppeService.loadByGruppeId(gruppe);
		
		if (selGruppe == null) {
			selGruppe = new Gruppe();
			selGruppe.setId(-1L);
			selGruppe.setName("Alle Gruppen");
		}
		
		List<Terminfindung> terminfindungenOffen;
		List<Terminfindung> terminfindungenAbgeschlossen;
		if (gruppe == -1L) {
			terminfindungenOffen =
				terminfindunguebersichtService.loadOffeneTerminfindungenFuerBenutzer(account);
			terminfindungenAbgeschlossen = terminfindunguebersichtService
				.loadAbgeschlosseneTerminfindungenFuerBenutzer(account);
		} else {
			terminfindungenOffen = terminfindunguebersichtService
				.loadOffeneTerminfindungenFuerGruppe(account, selGruppe.getId());
			terminfindungenAbgeschlossen = terminfindunguebersichtService
				.loadAbgeschlosseneTerminfindungenFuerGruppe(account, selGruppe.getId());
		}
		Terminuebersicht termine = new Terminuebersicht(terminfindungenAbgeschlossen,
			terminfindungenOffen, gruppen, selGruppe);
		
		m.addAttribute("termine", termine);
		
		return "termine";
	}
}
