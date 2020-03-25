package mops.termine2.controller;

import com.opencsv.CSVReader;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.imports.TerminFormatierung;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.LinkService;
import mops.termine2.services.TerminfindungService;
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

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
	private TerminfindungService terminfindungService;
	
	@Autowired
	private LinkService linkService;
	
	public TermineNeuController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/termine-neu")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String termineNeu(Principal p, Model m) {
		// Account
		Account account;
		if (p != null) {
			// Account
			account = authenticationService.createAccountFromPrincipal(p);
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
			terminfindung.setErgebnisVorFrist(true);
			m.addAttribute("terminfindung", terminfindung);
			m.addAttribute("fehler", "");
			authenticatedAccess.increment();
		} else {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		gruppen = gruppeService.sortGroupsByName(gruppen);
		m.addAttribute("gruppen", gruppen);
		Gruppe noGroup = new Gruppe();
		noGroup.setId(-1L);
		m.addAttribute("gruppeSelektiert", noGroup);
		
		// Terminfindung
		Terminfindung terminfindung = new Terminfindung();
		terminfindung.setVorschlaege(new ArrayList<>());
		terminfindung.getVorschlaege().add(null);
		terminfindung.setFrist(LocalDateTime.now().plusWeeks(1));
		terminfindung.setLoeschdatum(LocalDateTime.now().plusWeeks(4));
		
		m.addAttribute("terminfindung", terminfindung);
		m.addAttribute("fehler", "");
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "add")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neuerTermin(Principal p, Model m, Terminfindung terminfindung,
							  Gruppe gruppeSelektiert) {
		// Account
		Account account;
		if (p != null) {
			account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			authenticatedAccess.increment();
		} else {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		gruppen = gruppeService.sortGroupsByName(gruppen);
		m.addAttribute("gruppen", gruppen);
		
		// Selektierte Gruppe
		m.addAttribute("gruppeSelektiert", gruppeSelektiert);
		
		// Terminvorschlag hinzufügen
		List<LocalDateTime> termine = terminfindung.getVorschlaege();
		termine.add(null);
		
		m.addAttribute("terminfindung", terminfindung);
		m.addAttribute("fehler", "");
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "delete")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String terminLoeschen(Principal p, Model m, Terminfindung terminfindung, Gruppe gruppeSelektiert,
								 final HttpServletRequest request) {
		Account account;
		if (p != null) {
			// Account
			account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			authenticatedAccess.increment();
		} else {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		// Gruppen
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		gruppen = gruppeService.sortGroupsByName(gruppen);
		m.addAttribute("gruppen", gruppen);
		
		// Selektierte Gruppe
		m.addAttribute("gruppeSelektiert", gruppeSelektiert);
		
		// Terminvorschlag löschen
		terminfindung.getVorschlaege().remove(Integer.parseInt(request.getParameter("delete")));
		
		m.addAttribute("terminfindung", terminfindung);
		m.addAttribute("fehler", "");
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "create")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String terminfindungErstellen(Principal p, Model m, Terminfindung terminfindung,
										 Gruppe gruppeSelektiert,
										 RedirectAttributes ra) {
		String fehler = "";
		
		// Account
		Account account;
		if (p != null) {
			// Account
			account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			authenticatedAccess.increment();
		} else {
			throw new AccessDeniedException(Konstanten.NOT_LOGGED_IN);
		}
		
		// Vorschläge filtern. Doppelte und nicht gesetzte Daten löschen
		ArrayList<LocalDateTime> gueltigeVorschlaege = new ArrayList<LocalDateTime>();
		for (LocalDateTime ldt : terminfindung.getVorschlaege()) {
			if (ldt != null && !gueltigeVorschlaege.contains(ldt)) {
				gueltigeVorschlaege.add(ldt);
			}
		}
		
		if (gueltigeVorschlaege.isEmpty()) {
			gueltigeVorschlaege.add(null);
			fehler = "Es muss mindestens einen Vorschlag geben.";
		}
		
		terminfindung.setVorschlaege(gueltigeVorschlaege);
		
		// Terminfindung erstellen
		terminfindung.setErsteller(account.getName());
		if (gruppeSelektiert.getId() != null && gruppeSelektiert.getId() != -1) {
			Gruppe gruppe = gruppeService.loadByGruppeId(gruppeSelektiert.getId());
			terminfindung.setGruppeId(gruppe.getId());
		}
		
		if (terminfindung.getLink().isEmpty()) {
			String link = linkService.generiereEindeutigenLink();
			terminfindung.setLink(link);
		} else {
			if (!linkService.pruefeEindeutigkeitLink(terminfindung.getLink())) {
				fehler = "Der eingegebene Link existiert bereits.";
			}
			if (!linkService.isLinkValid(terminfindung.getLink())) {
				fehler = "Der eingegebene Link enthält ungültige Zeichen";
			}
		}
		
		if (!fehler.equals("")) {
			m.addAttribute("gruppen", gruppeService.loadByBenutzer(account));
			m.addAttribute("gruppeSelektiert", gruppeSelektiert);
			m.addAttribute("terminfindung", terminfindung);
			m.addAttribute("fehler", fehler);
			return "termine-neu";
		}
		
		terminfindungService.save(terminfindung);
		logger.info("Benutzer '" + account.getName() + "' hat eine neue Terminabstimmung mit link '"
			+ terminfindung.getLink() + "' erstellt");
		
		ra.addFlashAttribute("erfolg", "Der Termin wurde gespeichert.");
		return "redirect:/termine2";
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
					
					TerminFormatierung terminFormatierung =
						new TerminFormatierung(termineEingelesen);
					terminFormatierung.pruefeFuerJedenTerminGueltigesFormat(
						termineEingelesen, terminFormatierung.getDateTimeFormatter());
					terminFormatierung.pruefeObExistent(termineEingelesen);
					
					if (!terminFormatierung.pruefeObInZukunft(termineEingelesen,
							terminFormatierung.getDateTimeFormatter())) {
						m.addAttribute("message", "Die Termine sollten in der Zukunft liegen.");
						m.addAttribute("error", true);
					} else {
						
						// entferne ggf. überflüssige Datumsbox
						if (!terminFormatierung.pruefeObExistent(termineEingelesen)) {
							m.addAttribute("message", "Die Daten sollten existieren.");
							m.addAttribute("error", true);
						} else {
							// entferne ggf. überflüssige Datumsbox
							if (termine.get(0) == null) {
								terminfindung.getVorschlaege().remove(0);
							}
							
							// füge Termine ins Model ein
							for (String[] terminEingelesen : termineEingelesen) {
								LocalDateTime termin = LocalDateTime.parse(
										terminEingelesen[0] + ", "
												+ terminEingelesen[1],
										terminFormatierung
										.getDateTimeFormatter());
								termine.add(termin);
							}
							
							m.addAttribute("message", "Upload erfolgreich!");
							m.addAttribute("erfolg", true);
						}
					}
					
				} catch (Exception ex) {
					m.addAttribute("message",
						"Ein Fehler ist beim Verarbeiten der CSV-Datei aufgetreten. "
							+ "Alle Termine müssen im Format 'TT.MM.JJJJ,HH:MM' "
							+ "übergeben werden und sollten existente Daten sein.");
					m.addAttribute("error", true);
				}
			}
			m.addAttribute("gruppeSelektiert", gruppeSelektiert);
			m.addAttribute("terminfindung", terminfindung);
		}
		return "termine-neu";
	}
}
