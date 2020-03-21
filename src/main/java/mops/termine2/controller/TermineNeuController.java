package mops.termine2.controller;


import com.opencsv.CSVReader;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.LinkService;
import mops.termine2.services.TerminfindungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
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
		}
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "add")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String neuerTermin(Principal p, Model m, Terminfindung terminfindung,
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
			termine.add(LocalDateTime.now());
			
			// Selektierte Gruppe
			m.addAttribute("gruppeSelektiert", gruppeSelektiert);
			
			m.addAttribute("terminfindung", terminfindung);
		}
		
		return "termine-neu";
	}
	
	@PostMapping(path = "/termine-neu", params = "create")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String terminfindungErstellen(Principal p, Model m, Terminfindung terminfindung,
										 Gruppe gruppeSelektiert) {
		if (p != null) {
			authenticatedAccess.increment();
			
			// Account
			Account account = authenticationService.createAccountFromPrincipal(p);
			m.addAttribute(Konstanten.ACCOUNT, account);
			
			for (LocalDateTime ldt : terminfindung.getVorschlaege()) {
				if (ldt == null) {
					System.out.println("Fehler");
					// TODO: Fehlermeldung ausgeben und auf Terminfindung erstellen weiterleiten
				}
			}
			
			// Terminfindung erstellen
			terminfindung.setErsteller(account.getName());
			terminfindung.setLoeschdatum(terminfindung.getFrist().plusWeeks(3));
			if (gruppeSelektiert.getId() != null && gruppeSelektiert.getId() != -1) {
				Gruppe gruppe = gruppeService.loadById(gruppeSelektiert.getId());
				terminfindung.setGruppe(gruppe.getName());
			}
			
			String link = linkService.generiereEindeutigenLink();
			terminfindung.setLink(link);
			
			terminfindungService.save(terminfindung);
		}
		
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
					DateTimeFormatter formatter = setzeFormatFuerTermine();
					pruefeFuerJedenTerminGueltigesFormat(termineEingelesen, formatter);
					
					// entferne ggf. überflüssige Datumsbox
					if (termine.get(0) == null) {
						terminfindung.getVorschlaege().remove(0);
					}
					
					fuegeTermineInModelEin(termineEingelesen, formatter, termine);
					m.addAttribute("message", "Upload erfolgreich!");
					m.addAttribute("erfolg", true);
					
				} catch (Exception ex) {
					m.addAttribute("message",
						"Ein Fehler ist beim Verarbeiten der CSV-Datei aufgetreten. "
							+ "Alle Termine müssen im Format 'TT.MM.JJJJ,HH:MM' "
							+ "übergeben werden.");
					m.addAttribute("error", true);
				}
				
				m.addAttribute("gruppeSelektiert", gruppeSelektiert);
				m.addAttribute("terminfindung", terminfindung);
			}
		}
		return "termine-neu";
	}
	
	private DateTimeFormatter setzeFormatFuerTermine() {
		DateTimeFormatterBuilder b = new DateTimeFormatterBuilder();
		return b.appendPattern("dd.MM.")
			.appendValue(ChronoField.YEAR_OF_ERA, 4, 4,
				SignStyle.EXCEEDS_PAD).appendPattern(" HH:mm")
			.toFormatter();
	}
	
	private void pruefeFuerJedenTerminGueltigesFormat(
		List<String[]> termineEingelesen, DateTimeFormatter formatter) {
		for (String[] terminEingelesen : termineEingelesen) {
			LocalDateTime.parse(terminEingelesen[0]
				+ " " + terminEingelesen[1], formatter);
		}
	}
	
	private void fuegeTermineInModelEin(
		List<String[]> termineEingelesen, DateTimeFormatter formatter, List<LocalDateTime> termine) {
		for (String[] terminEingelesen : termineEingelesen) {
			LocalDateTime termin = LocalDateTime.parse(terminEingelesen[0]
				+ " " + terminEingelesen[1], formatter);
			termine.add(termin);
		}
	}
}
