package mops.termine2.controller;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.LinkService;
import mops.termine2.services.TerminfindungService;
import mops.termine2.util.CSVHelper;
import mops.termine2.util.IntegerToolkit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class TermineNeuController {
	
	private final Logger logger = Logger.getLogger(TermineNeuController.class.getName());
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private LinkService linkService;
	
	@Autowired
	private TerminfindungService terminfindungService;
	
	public TermineNeuController(MeterRegistry registry) {
		authenticatedAccess = registry.counter(Konstanten.ACCESS_AUTHENTICATED);
	}
	
	@GetMapping("/termine-neu")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineNeu(Principal principal, Model model) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzerSortiert(account);
		// Terminfindung
		Terminfindung terminfindung = terminfindungService.createDefaultTerminfindung();
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_GRUPPEN, gruppen);
		model.addAttribute(Konstanten.MODEL_GRUPPE_SELEKTIERT, gruppeService.erstelleStandardGruppe());
		model.addAttribute(Konstanten.MODEL_TERMINFINDUNG, terminfindung);
		model.addAttribute(Konstanten.MODEL_FEHLER, "");
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "add")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neuerTermin(Principal principal, Model model, Terminfindung terminfindung,
		Gruppe gruppeSelektiert) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzerSortiert(account);
		// Terminvorschlag hinzufügen
		List<LocalDateTime> termine = terminfindung.getVorschlaege();
		termine.add(null);
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_GRUPPEN, gruppen);
		model.addAttribute(Konstanten.MODEL_GRUPPE_SELEKTIERT, gruppeSelektiert);
		model.addAttribute(Konstanten.MODEL_TERMINFINDUNG, terminfindung);
		model.addAttribute(Konstanten.MODEL_FEHLER, "");
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "delete")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String terminLoeschen(Principal principal, Model model,
		Terminfindung terminfindung, Gruppe gruppeSelektiert,
		final HttpServletRequest request) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzerSortiert(account);
		// Terminvorschlag löschen
		int indexToDelete = IntegerToolkit.getInt(request.getParameter("delete"));
		terminfindungService.loescheTermin(terminfindung, indexToDelete);
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_GRUPPEN, gruppen);
		model.addAttribute(Konstanten.MODEL_GRUPPE_SELEKTIERT, gruppeSelektiert);
		model.addAttribute(Konstanten.MODEL_TERMINFINDUNG, terminfindung);
		model.addAttribute(Konstanten.MODEL_FEHLER, "");
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "create")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String terminfindungErstellen(Principal principal, Model model, Terminfindung terminfindung,
		Gruppe gruppeSelektiert, RedirectAttributes redirectAttributes) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}
		
		List<String> fehler = terminfindungService.erstelleTerminfindung(account,
			terminfindung);
		fehler.addAll(linkService.setzeOderPruefeLink(terminfindung));
		gruppeService.setzeGruppeId(terminfindung, gruppeSelektiert);
		
		if (fehler.isEmpty()) {
			terminfindungService.save(terminfindung);
			logger.info("Benutzer '" + account.getName() + "' hat eine neue Terminabstimmung mit Link '"
				+ terminfindung.getLink() + "' erstellt");
			
			redirectAttributes.addFlashAttribute(Konstanten.MODEL_ERFOLG, 
				Konstanten.MESSAGE_TERMIN_GESPEICHERT);
			return "redirect:/termine2";
		}
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_GRUPPEN, gruppeService.loadByBenutzer(account));
		model.addAttribute(Konstanten.MODEL_GRUPPE_SELEKTIERT, gruppeSelektiert);
		model.addAttribute(Konstanten.MODEL_TERMINFINDUNG, terminfindung);
		model.addAttribute(Konstanten.MODEL_FEHLER, fehler.get(fehler.size() - 1));
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "upload", consumes = "multipart/form-data")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String uploadTermineCSV(@RequestParam("file") MultipartFile file, Principal principal,
		Model model, Terminfindung terminfindung, Gruppe gruppeSelektiert) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}
		
		// Terminvorschlag hinzufügen
		List<LocalDateTime> termine = terminfindung.getVorschlaege();
		
		List<String> fehler = CSVHelper.readCSV(file, terminfindung, termine);
		
		// If any of the Termine lies before the Frist, then the Frist has to be
		// updated.
		terminfindungService.aktualisiereFristUndLoeschdatum(terminfindung, termine);
		
		model.addAttribute(Konstanten.MODEL_ACCOUNT, account);
		model.addAttribute(Konstanten.MODEL_GRUPPEN, gruppeService.loadByBenutzerSortiert(account));
		model.addAttribute(Konstanten.MODEL_GRUPPE_SELEKTIERT, gruppeSelektiert);
		model.addAttribute(Konstanten.MODEL_TERMINFINDUNG, terminfindung);
		
		if (fehler.isEmpty()) {
			model.addAttribute(Konstanten.MODEL_MESSAGE, Konstanten.MESSAGE_CSV_ERFOLG);
			model.addAttribute(Konstanten.MODEL_ERFOLG, true);
		} else {
			model.addAttribute(Konstanten.MODEL_MESSAGE, fehler.get(fehler.size() - 1));
			model.addAttribute(Konstanten.MODEL_ERROR, true);
		}
		
		return "termine-neu";
	}
	
	@RequestMapping(path = "/termine-neu", params = "download")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public void termineRunterladen(Principal principal, Terminfindung terminfindung,
		Model model, HttpServletResponse response) 
		throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.ERROR_NOT_LOGGED_IN);
		}		
		CSVHelper.exportCSV(terminfindung, response);				
	}
	
}
