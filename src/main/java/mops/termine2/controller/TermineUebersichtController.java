package mops.termine2.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;

import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.Terminuebersicht;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.TerminfindungService;
import mops.termine2.services.TerminfindungUebersichtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

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
	private TerminfindungUebersichtService terminfindunguebersichtService;
	
	@Autowired
	private TerminfindungService terminfindungService;
	
	public TermineUebersichtController(MeterRegistry registry) {
		authenticatedAccess = registry.counter(Konstanten.ACCESS_AUTHENTICATED);
	}
	
	@GetMapping("")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String index(Principal principal, Model model,
		@RequestParam(name = "gruppe", defaultValue = "-1") String gruppeId) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}
		
		if (gruppeService.pruefeGruppenzugriffVerweigert(account, gruppeId)) {
			throw new AccessDeniedException(Konstanten.ERROR_GROUP_ACCESS_DENIED);
		}
		
		List<Gruppe> gruppen = gruppeService.loadByBenutzerSortiert(account);		
		
		Gruppe selGruppe = gruppeService.loadByGruppeIdOderStandard(gruppeId);
		
		List<Terminfindung> terminfindungenOffen =
			terminfindunguebersichtService.loadOffeneTerminfindungen(account, selGruppe);
		List<Terminfindung> terminfindungenAbgeschlossen =
			terminfindunguebersichtService.loadAbgeschlosseneTerminfindungen(account, selGruppe);
		
		HashMap<String, String> groups = gruppeService.extrahiereIdAndNameAusGruppen(gruppen);
		terminfindungService.setzeGruppenName(terminfindungenOffen, groups);
		terminfindungService.setzeGruppenName(terminfindungenAbgeschlossen, groups);
		
		Terminuebersicht termine = new Terminuebersicht(terminfindungenAbgeschlossen,
			terminfindungenOffen, gruppen, selGruppe);
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_TERMINE, termine);
		
		return "termine";
	}
	
}
