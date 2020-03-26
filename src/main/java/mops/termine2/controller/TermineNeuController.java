package mops.termine2.controller;


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
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.filehandling.TerminFormatierung;
import mops.termine2.services.LinkService;
import mops.termine2.services.TerminfindungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class TermineNeuController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private TerminfindungService terminfindungService;
	
	@Autowired
	private LinkService linkService;
	
	public TermineNeuController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/termine-neu")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineNeu(Principal p, Model m) {
		if (p != null) {
			authenticatedAccess.increment();
			
			/* Account */
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			/* Gruppen */
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			m.addAttribute("gruppen", gruppen);
			Gruppe noGroup = new Gruppe();
			noGroup.setId(-1L);
			m.addAttribute("gruppeSelektiert", noGroup);
			
			Terminfindung terminfindung = new Terminfindung();
			terminfindung.setVorschlaege(new ArrayList<>());
			terminfindung.getVorschlaege().add(LocalDateTime.now());
			terminfindung.setFrist(LocalDateTime.now().plusWeeks(1));
			
			m.addAttribute("terminfindung", terminfindung);
			m.addAttribute("fehler", "");
		}
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "add")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neuerTermin(Principal p, Model m, Terminfindung terminfindung,
							  Gruppe gruppeSelektiert) {
		if (p != null) {
			authenticatedAccess.increment();
			
			/* Account */
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			/* Gruppen */
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			m.addAttribute("gruppen", gruppen);
			
			/* Terminvorschlag hinzufügen */
			List<LocalDateTime> termine = terminfindung.getVorschlaege();
			termine.add(LocalDateTime.now());
			
			/* Selektierte Gruppe */
			m.addAttribute("gruppeSelektiert", gruppeSelektiert);
			
			m.addAttribute("terminfindung", terminfindung);
			m.addAttribute("fehler", "");
		}
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "create")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String terminfindungErstellen(Principal p, Model m, Terminfindung terminfindung,
			Gruppe gruppeSelektiert, RedirectAttributes ra) {
		String fehler = "";
		
		if (p != null) {
			authenticatedAccess.increment();
			
			// Account
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			ArrayList<LocalDateTime> gueltigeVorschlaege = new ArrayList<LocalDateTime>();
			
			for (LocalDateTime ldt : terminfindung.getVorschlaege()) {
				if (ldt != null) {
					gueltigeVorschlaege.add(ldt);
				}
			}
			
			if (gueltigeVorschlaege.isEmpty()) {
				gueltigeVorschlaege.add(null);
				fehler = "Es muss mindestens einen Vorschlag geben.";
			}
			
			terminfindung.setVorschlaege(gueltigeVorschlaege);
			
			if (!fehler.equals("")) {
				m.addAttribute("gruppen", gruppeService.loadByBenutzer(account));
				m.addAttribute("gruppeSelektiert", gruppeSelektiert);
				m.addAttribute("terminfindung", terminfindung);
				m.addAttribute("fehler", fehler);
				
				return "termine-neu";
			}
			
			// Terminfindung erstellen
			terminfindung.setErsteller(account.getName());
			terminfindung.setLoeschdatum(terminfindung.getFrist().plusWeeks(3));
			if (gruppeSelektiert.getId() != null && gruppeSelektiert.getId() != -1) {
				Gruppe gruppe = gruppeService.loadByGruppeId(gruppeSelektiert.getId());
				terminfindung.setGruppeId(gruppe.getId());
			}
			
			String link = linkService.generiereEindeutigenLink();
			terminfindung.setLink(link);
			
			terminfindungService.save(terminfindung);
		}
		
		ra.addFlashAttribute("erfolg", "Der Termin wurde gespeichert.");
		return "redirect:/termine2";
	}
	
	@PostMapping(path = "/termine-neu", params = "delete")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String terminLoeschen(Principal p, Model m, Terminfindung terminfindung, Gruppe gruppeSelektiert,
			final HttpServletRequest request) {
		if (p != null) {
			authenticatedAccess.increment();
			
			// Account
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			// Gruppen
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			m.addAttribute("gruppen", gruppen);
			
			// Selektierte Gruppe
			m.addAttribute("gruppeSelektiert", gruppeSelektiert);
			
			// Terminvorschlag löschen
			terminfindung.getVorschlaege().remove(Integer.parseInt(request.getParameter("delete")));
			
			m.addAttribute("terminfindung", terminfindung);
			m.addAttribute("fehler", "");
		}
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "upload", consumes = "multipart/form-data")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String uploadTermineCSV(@RequestParam("file") MultipartFile file, Principal p,
								   Model m, Terminfindung terminfindung,
								   Gruppe gruppeSelektiert) {
		if (p != null) {
			authenticatedAccess.increment();
			
			// Account
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			// Gruppen
			List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
			m.addAttribute("gruppen", gruppen);
			
			// Terminvorschlag hinzufügen
			List<LocalDateTime> termine = terminfindung.getVorschlaege();
			
			if (file.isEmpty()) {
				m.addAttribute("message", "Bitte eine CSV-Datei zum Upload auswählen.");
				m.addAttribute("error", true);
			} else {
				try (CSVReader csvReader = new CSVReader(
					new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
					
					List<String[]> termineEingelesen = csvReader.readAll();
					if ("DATUM".equals(termineEingelesen.get(0)[0])) {
						termineEingelesen.remove(0);
					}
					
					TerminFormatierung terminFormatierung =
						new TerminFormatierung(termineEingelesen);
					
					if (!terminFormatierung.pruefeObGueltigesFormat(
						termineEingelesen, terminFormatierung.getDateTimeFormatter())) {
						m.addAttribute("message",
							"Alle Termine müssen im Format "
								+ "'TT.MM.JJJJ,HH:MM' übergeben werden und "
								+ "sollten existente Daten sein.");
						m.addAttribute("error", true);
					} else if (!terminFormatierung.pruefeObInZukunft(termineEingelesen,
							terminFormatierung.getDateTimeFormatter())) {
						m.addAttribute("message", "Die Termine sollten in der Zukunft liegen.");
						m.addAttribute("error", true);
					} else if (!terminFormatierung.pruefeObGueltigesDatum(termineEingelesen)) {
						m.addAttribute("message",
							"Die Termine sollten existieren.");
						m.addAttribute("error", true);
					} else {
						// entferne ggf. überflüssige Datumsbox
						if (termine.get(0) == null) {
							terminfindung.getVorschlaege().remove(0);
						}
						
						// füge Termine ins Model ein
						for (String[] terminEingelesen : termineEingelesen) {
							LocalDateTime termin = LocalDateTime.parse(terminEingelesen[0]
									+ ", " + terminEingelesen[1], terminFormatierung
									.getDateTimeFormatter());
							termine.add(termin);
						}
						m.addAttribute("message", "Upload erfolgreich!");
						m.addAttribute("erfolg", true);
					}
					
				} catch (RuntimeException ex) {
					throw ex;
				} catch (Exception ex) {
					m.addAttribute("message",
						"Ein Fehler ist beim Verarbeiten der CSV-Datei aufgetreten. ");
					m.addAttribute("error", true);
				}
			}
			m.addAttribute("gruppeSelektiert", gruppeSelektiert);
			m.addAttribute("terminfindung", terminfindung);
		}
		return "termine-neu";
	}
	
	@RequestMapping(path = "/termine-neu", params = "download")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public void termineRunterladen(Principal p, Terminfindung terminfindung,
								   Model m, HttpServletResponse response)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		if (p != null) {
			authenticatedAccess.increment();
			
			// Account
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			String filename = "termine.csv";
			List<LocalDateTime> termine = terminfindung.getVorschlaege();
			ExportCSV exportCSV = new ExportCSV(termine);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"" + filename + "\"");
			response.setContentType("text/csv");
			
			if (termine.get(0) != null) {
				StatefulBeanToCsv<ExportFormat> writer = new StatefulBeanToCsvBuilder<ExportFormat>(
						response.getWriter()).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
						.withOrderedResults(false)
						.build();
				writer.write(exportCSV.localDateTimeZuExportFormat());
			}
		}
	}
	
}
