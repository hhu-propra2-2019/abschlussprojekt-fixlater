package mops.termine2.services;

import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bietet Methoden zur Filterung von Terminfindungen bezüglich der Frist
 * für einzelne Benutzer oder ganze Gruppen
 */
@Service
public class TerminfindungUebersichtService {
	
	@Autowired
	private TerminfindungService terminfindungService;
	
	@Autowired
	private TerminfindungAntwortService terminAntwortService;
	
	@Autowired
	private GruppeService gruppeService;
	
	public TerminfindungUebersichtService(TerminfindungService terminfindungService,
										  GruppeService gruppeService,
										  TerminfindungAntwortService
											  terminAntwortService) {
		this.terminfindungService = terminfindungService;
		this.gruppeService = gruppeService;
		this.terminAntwortService = terminAntwortService;
	}
	
	/**
	 * Holt alle noch offenen Terminfindungen zu der Gruppe mit 
	 * Gruppen-ID {@code gruppeId} aus der Datenbank. 
	 * Ist {@code gruppe} {@code null} oder hat ID "-1", so werden
	 * die offenen Terminfindungen für den Benutzer aus der Datenbank geholt
	 * ohne Filterung nach einer Gruppe.
	 * Eine offene Terminfindung ist dabei eine Terminfindung, deren Frist
	 * in der Zukunft liegt.
	 *
	 * @param account Das Account Objekt des aktuellen Benutzers
	 * @param gruppe Das Gruppen Objekt der ausgewählten Gruppe
	 * 
	 * @return die noch offenen Terminfindungen für die Gruppe oder den Benutzer
	 */
	public List<Terminfindung> loadOffeneTerminfindungen(Account account, Gruppe gruppe) {
		if (gruppe == null || gruppe.getId().equals("-1")) {
			return loadOffeneTerminfindungenFuerBenutzer(account);
		}
		return loadOffeneTerminfindungenFuerGruppe(account, gruppe.getId());
	}
	
	/**
	 * Holt alle schon abgeschlossenen Terminfindungen zu der Gruppe mit 
	 * Gruppen-ID {@code gruppeId} aus der Datenbank. 
	 * Ist {@code gruppe} {@code null} oder hat ID "-1", so werden
	 * die abgeschlossenen Terminfindungen für den Benutzer aus der Datenbank geholt
	 * ohne Filterung nach einer Gruppe.
	 * Eine abgeschlossene Terminfindung ist dabei eine Terminfindung, deren Frist
	 * in der Vergangenheit liegt.
	 *
	 * @param account Das Account Objekt des aktuellen Benutzers
	 * @param gruppe Das Gruppen Objekt der ausgewählten Gruppe
	 * 
	 * @return die bereits abgeschlossenen Terminfindungen für die Gruppe oder den Benutzer
	 */
	public List<Terminfindung> loadAbgeschlosseneTerminfindungen(Account account, Gruppe gruppe) {
		if (gruppe == null || gruppe.getId().equals("-1")) {
			return loadAbgeschlosseneTerminfindungenFuerBenutzer(account);
		}
		return loadAbgeschlosseneTerminfindungenFuerGruppe(account, gruppe.getId());
	}
	
	private List<Terminfindung> loadOffeneTerminfindungenFuerGruppe(Account account, String gruppeId) {
		List<Terminfindung> termine = new ArrayList<>();
		termine.addAll(terminfindungService.loadByGruppeOhneTermine(gruppeId));
		List<Terminfindung> offeneTermine = filtereOffeneTerminfindungen(termine);
		
		// Nach Frist sortieren
		offeneTermine = offeneTermine.stream()
			.sorted(Comparator.comparing(Terminfindung::getFrist))
			.collect(Collectors.toList());
		
		offeneTermine = setTeilgenommen(offeneTermine, account);
		
		return offeneTermine;
	}
	
	private List<Terminfindung> loadAbgeschlosseneTerminfindungenFuerGruppe(Account account, String gruppeId) {
		List<Terminfindung> termine = new ArrayList<>();
		termine.addAll(terminfindungService.loadByGruppeOhneTermine(gruppeId));
		List<Terminfindung> abgeschlosseneTermine = filtereAbgeschlosseneTerminfindungen(termine);
		
		// sortieren
		abgeschlosseneTermine = sortiereAbgeschlosseneTermine(abgeschlosseneTermine);
		
		return abgeschlosseneTermine;
	}
	
	private List<Terminfindung> loadOffeneTerminfindungenFuerBenutzer(Account account) {
		List<Terminfindung> termine = getAllTerminfindungenVonBenutzer(account);
		List<Terminfindung> offeneTermine = filtereOffeneTerminfindungen(termine);
		
		// Nach Frist sortieren
		offeneTermine = offeneTermine.stream()
			.sorted(Comparator.comparing(Terminfindung::getFrist))
			.collect(Collectors.toList());
		
		offeneTermine = setTeilgenommen(offeneTermine, account);
		
		return offeneTermine;
	}
	
	private List<Terminfindung> loadAbgeschlosseneTerminfindungenFuerBenutzer(Account account) {
		List<Terminfindung> termine = getAllTerminfindungenVonBenutzer(account);
		List<Terminfindung> abgeschlosseneTermine = filtereAbgeschlosseneTerminfindungen(termine);
		
		// sortieren
		abgeschlosseneTermine = sortiereAbgeschlosseneTermine(abgeschlosseneTermine);
		
		return abgeschlosseneTermine;
	}
	
	private List<Terminfindung> eindeutigeTerminfindungsListe(List<Terminfindung> termine) {
		List<Terminfindung> distinct = new ArrayList<>();
		List<String> links = new ArrayList<>();
		
		for (Terminfindung termin : termine) {
			if (!links.contains(termin.getLink())) {
				links.add(termin.getLink());
				distinct.add(termin);
			}
		}
		
		return distinct;
	}
	
	private List<Terminfindung> getAllTerminfindungenVonBenutzer(Account account) {
		List<Terminfindung> termine = getTermineVonBenutzer(account);
		termine.addAll(terminfindungService.loadAllBenutzerHatAbgestimmtOhneTermine(account.getName()));
		
		// doppelte Termine löschen
		termine = eindeutigeTerminfindungsListe(termine);
		
		return termine;
	}
	
	private List<Terminfindung> sortiereAbgeschlosseneTermine(List<Terminfindung> termine) {
		
		List<Terminfindung> termineInVergangenheit = new ArrayList<>();
		List<Terminfindung> termineInZukunft = new ArrayList<>();
		
		// Aufteilen in Termine, die in der Vergangenheit bzw. in der Zukunft liegen
		for (Terminfindung termin : termine) {
			if (termin.getErgebnis().compareTo(LocalDateTime.now()) <= 0) {
				termineInVergangenheit.add(termin);
			} else {
				termineInZukunft.add(termin);
			}
		}
		
		// Listen sortieren
		termineInVergangenheit = termineInVergangenheit.stream()
			.sorted(Comparator.comparing(Terminfindung::getErgebnis))
			.collect(Collectors.toList());
		
		termineInZukunft = termineInZukunft.stream()
			.sorted(Comparator.comparing(Terminfindung::getErgebnis))
			.collect(Collectors.toList());
		
		// Zuerst die Termine, die noch bevorstehen und danach die bereits stattgefundenen Termine
		List<Terminfindung> terminesortiert = new ArrayList<>();
		terminesortiert.addAll(termineInZukunft);
		terminesortiert.addAll(termineInVergangenheit);
		
		return terminesortiert;
	}
	
	private List<Terminfindung> getTermineVonBenutzer(Account account) {
		List<Terminfindung> termine = new ArrayList<>();
		termine.addAll(terminfindungService.loadByErstellerOhneTermine(account.getName()));
		
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		for (Gruppe g : gruppen) {
			termine.addAll(terminfindungService.loadByGruppeOhneTermine(g.getId()));
		}
		
		return termine;
	}
	
	private List<Terminfindung> filtereOffeneTerminfindungen(List<Terminfindung> termine) {
		List<Terminfindung> offeneTermine = new ArrayList<>();
		for (Terminfindung termin : termine) {
			if (termin.getFrist().compareTo(LocalDateTime.now()) > 0) {
				offeneTermine.add(termin);
			}
		}
		return offeneTermine;
	}
	
	private List<Terminfindung> filtereAbgeschlosseneTerminfindungen(List<Terminfindung> termine) {
		List<Terminfindung> abgeschlosseneTermine = new ArrayList<>();
		for (Terminfindung termin : termine) {
			if (termin.getFrist().compareTo(LocalDateTime.now()) <= 0) {
				abgeschlosseneTermine.add(termin);
			}
		}
		return abgeschlosseneTermine;
	}
	
	private List<Terminfindung> setTeilgenommen(List<Terminfindung> termine, Account account) {
		for (Terminfindung termin : termine) {
			termin.setTeilgenommen(
				terminAntwortService.hatNutzerAbgestimmt(account.getName(), termin.getLink()));
		}
		return termine;
	}
	
}
