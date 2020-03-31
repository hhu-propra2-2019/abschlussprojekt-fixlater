package mops.termine2.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.filehandling.ExportCSV;
import mops.termine2.filehandling.ExportFormat;
import mops.termine2.filehandling.TerminFormatierung;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.LinkService;
import mops.termine2.services.TerminfindungService;
import mops.termine2.util.IntegerToolkit;
import mops.termine2.util.LocalDateTimeManager;

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
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/termine-neu")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineNeu(Principal principal, Model model) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzerSorted(account);
		// Terminfindung
		Terminfindung terminfindung = terminfindungService.createDefaultTerminfindung();
		
		model.addAttribute(Konstanten.ACCOUNT, account);
		model.addAttribute("gruppen", gruppen);
		model.addAttribute("gruppeSelektiert", gruppeService.createDefaultGruppe());		
		model.addAttribute("terminfindung", terminfindung);
		model.addAttribute("fehler", "");
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "add")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neuerTermin(Principal principal, Model model, Terminfindung terminfindung,
		Gruppe gruppeSelektiert) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzerSorted(account);		
		// Terminvorschlag hinzufügen
		List<LocalDateTime> termine = terminfindung.getVorschlaege();
		termine.add(null);

		model.addAttribute(Konstanten.ACCOUNT, account);
		model.addAttribute("gruppen", gruppen);
		model.addAttribute("gruppeSelektiert", gruppeSelektiert);		
		model.addAttribute("terminfindung", terminfindung);
		model.addAttribute("fehler", "");
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "delete")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String terminLoeschen(Principal principal, Model model, 
		Terminfindung terminfindung, Gruppe gruppeSelektiert,
		final HttpServletRequest request) {
		
		//Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzerSorted(account);
		// Terminvorschlag löschen
		int indexToDelete = IntegerToolkit.getInt(request.getParameter("delete"));
		terminfindungService.loescheTermin(terminfindung, indexToDelete);	
		
		model.addAttribute(Konstanten.ACCOUNT, account);
		model.addAttribute("gruppen", gruppen);
		model.addAttribute("gruppeSelektiert", gruppeSelektiert);
		model.addAttribute("terminfindung", terminfindung);
		model.addAttribute("fehler", "");
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "create")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String terminfindungErstellen(Principal principal, Model model, Terminfindung terminfindung,
		Gruppe gruppeSelektiert, RedirectAttributes redirectAttributes) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		List<String> fehler = terminfindungService.erstelleTerminfindung(account, 
			terminfindung);
		fehler.addAll(linkService.setzeLink(terminfindung));
		gruppeService.setzeGruppeId(terminfindung, gruppeSelektiert);		
		
		if (fehler.isEmpty()) {
			terminfindungService.save(terminfindung);
			logger.info("Benutzer '" + account.getName() + "' hat eine neue Terminabstimmung mit Link '"
				+ terminfindung.getLink() + "' erstellt");
			
			redirectAttributes.addFlashAttribute("erfolg", "Der Termin wurde gespeichert.");
			return "redirect:/termine2";
		}
		
		model.addAttribute(Konstanten.ACCOUNT, account);
		model.addAttribute("gruppen", gruppeService.loadByBenutzer(account));
		model.addAttribute("gruppeSelektiert", gruppeSelektiert);
		model.addAttribute("terminfindung", terminfindung);
		model.addAttribute("fehler", fehler.get(fehler.size() - 1));
		
		return "termine-neu";
	}
	
	//TODO: CSV Methoden refactoren
	@PostMapping(path = "/termine-neu", params = "upload", consumes = "multipart/form-data")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String uploadTermineCSV(@RequestParam("file") MultipartFile file, Principal principal,
		Model model, Terminfindung terminfindung, Gruppe gruppeSelektiert) {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		model.addAttribute(Konstanten.ACCOUNT, account);
		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		model.addAttribute("gruppen", gruppen);
		
		// Terminvorschlag hinzufügen
		List<LocalDateTime> termine = terminfindung.getVorschlaege();
		
		if (file.isEmpty()) {
			model.addAttribute("message", "Bitte eine CSV-Datei zum Upload auswählen.");
			model.addAttribute("error", true);
		} else {
			try (CSVReader csvReader = new CSVReader(
				new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
				
				List<String[]> termineEingelesen = csvReader.readAll();
				if ("DATUM".equals(termineEingelesen.get(0)[0])) {
					termineEingelesen.remove(0);
				}
				
				TerminFormatierung terminFormatierung = new TerminFormatierung(termineEingelesen);
				
				if (!terminFormatierung.pruefeObGueltigesFormat()) {
					model.addAttribute("message",
						"Alle Termine müssen im Format "
							+ "'TT.MM.JJJJ,HH:MM' übergeben werden und "
							+ "sollten existente Daten sein.");
					model.addAttribute("error", true);
				} else if (!terminFormatierung.pruefeObInZukunft()) {
					model.addAttribute("message", "Die Termine sollten in der Zukunft liegen.");
					model.addAttribute("error", true);
				} else if (!terminFormatierung.pruefeObGueltigesDatum()) {
					model.addAttribute("message",
						"Die Termine sollten existieren.");
					model.addAttribute("error", true);
				} else {
					// entferne ggf. überflüssige Datumsbox
					if (termine.get(0) == null) {
						terminfindung.getVorschlaege().remove(0);
					}
					
					// füge Termine ins Model ein
					for (String[] terminEingelesen : termineEingelesen) {
						LocalDateTime termin = LocalDateTime.parse(terminEingelesen[0]
							+ ", " + terminEingelesen[1],
							terminFormatierung
								.getDateTimeFormatter());
						termine.add(termin);
					}
					model.addAttribute("message", "Upload erfolgreich!");
					model.addAttribute("erfolg", true);
				}
				
			} catch (RuntimeException ex) {
				throw ex;
			} catch (Exception ex) {
				model.addAttribute("message",
					"Ein Fehler ist beim Verarbeiten der CSV-Datei aufgetreten. ");
				model.addAttribute("error", true);
			}
		}
			
		// If any of the Termine lies before the Frist, then the Frist has to be
		// updated.
		ArrayList<LocalDateTime> gueltigeVorschlaege = LocalDateTimeManager
			.filterUngueltigeDaten(terminfindung.getVorschlaege());
		LocalDateTime minVorschlag = LocalDateTimeManager
			.bekommeFruehestesDatum(gueltigeVorschlaege);
		LocalDateTime maxVorschlag = LocalDateTimeManager
			.bekommeSpaetestesDatum(gueltigeVorschlaege);
		
		// minVorschlag and maxVorschlag are always null together
		if (minVorschlag != null) {
			terminfindungService.setzeFrist(terminfindung, minVorschlag);
			terminfindungService.setzeLoeschdatum(terminfindung, maxVorschlag);
		}		
		
		model.addAttribute("gruppeSelektiert", gruppeSelektiert);
		model.addAttribute("terminfindung", terminfindung);
		
		return "termine-neu";
	}
	
	@RequestMapping(path = "/termine-neu", params = "download")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public void termineRunterladen(Principal principal, Terminfindung terminfindung,
		Model model, HttpServletResponse response)
		throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		
		// Account
		Account account = authenticationService.checkLoggedIn(principal, authenticatedAccess);
		if (account == null) {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
			"attachment; filename=\"termine.csv\"");
		response.setContentType("text/csv");

		List<LocalDateTime> termine = terminfindung.getVorschlaege();
		if (termine.get(0) != null) {
			StatefulBeanToCsv<ExportFormat> writer = new StatefulBeanToCsvBuilder<ExportFormat>(
				response.getWriter()).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
					.withOrderedResults(false)
					.build();
			writer.write(new ExportCSV(termine).localDateTimeZuExportFormat());
		}
		
	}
	
}
