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

@Service
public class TerminfindunguebersichtService {
	
	@Autowired
	private TerminfindungService terminfindungService;
	
	@Autowired
	private GruppeService gruppeService;
	
	public TerminfindunguebersichtService(TerminfindungService terminfindungService, GruppeService gruppeService) {
		this.terminfindungService = terminfindungService;
		this.gruppeService = gruppeService;
	}
	
	/**
	 * Geht die Termine durch und filtert nach offenen die zu einer Gruppe gehören
	 * @param gruppe
	 * @return eine Liste von offenen Terminabstimmungen nach Gruppe
	 */
	public List<Terminfindung> loadOffeneTerminfindungenFuerGruppe(String gruppe) {
		List<Terminfindung> termine = new ArrayList<>();
		List<Terminfindung> offeneTermine = new ArrayList<>();
		
		termine.addAll(terminfindungService.loadByGruppeOhneTermine(gruppe));
		
		for (Terminfindung termin : termine) {
			if (termin.getFrist().compareTo(LocalDateTime.now()) > 0) {
				offeneTermine.add(termin);
			}
		}
		return offeneTermine;
	}
	
	/**
	 * Geht die Termine durch und filtert nach abgeschlossenen die zu einer Gruppe gehören
	 * @param gruppe
	 * @return eine Liste von abgeschlossenen Terminabstimmungen nach Gruppe
	 */
	public List<Terminfindung> loadAbgeschlosseneTerminfindungenFuerGruppe(String gruppe) {
		List<Terminfindung> termine = new ArrayList<>();
		List<Terminfindung> abgeschlosseneTermine = new ArrayList<>();
		
		termine.addAll(terminfindungService.loadByGruppeOhneTermine(gruppe));
		
		// Abgeschlossene Termine filtern
		for (Terminfindung termin : termine) {
			if (termin.getFrist().compareTo(LocalDateTime.now()) <= 0) {
				abgeschlosseneTermine.add(termin);
			}
		}
		
		// sortieren
		abgeschlosseneTermine = sortiereTermine(abgeschlosseneTermine);
		
		return abgeschlosseneTermine;
	}
	
	/**
	 * Geht die Termine durch und filtert nach offenen die zu einem Nutzer gehören
	 * @param account
	 * @return eine Liste von offenen Terminabstimmungen nach Nutzer
	 */
	public List<Terminfindung> loadOffeneTerminfindungenFuerBenutzer(Account account) {
		List<Terminfindung> termine = getAllTerminfindungenVonBenutzer(account);
		
		List<Terminfindung> offeneTermine = new ArrayList<>();
		for (Terminfindung termin : termine) {
			if (termin.getFrist().compareTo(LocalDateTime.now()) > 0) {
				offeneTermine.add(termin);
			}
		}
		
		return offeneTermine;
	}
	
	/**
	 * Geht die Termine durch und filtert nach abgeschlossenen die zu einem Nutzer gehören
	 * @param account
	 * @return eine Liste von abgeschlossenen Terminabstimmungen nach Nutzer
	 */
	public List<Terminfindung> loadAbgeschlosseneTerminfindungenFuerBenutzer(Account account) {
		List<Terminfindung> termine = getAllTerminfindungenVonBenutzer(account);
		
		// Abgeschlossene Termine filtern
		List<Terminfindung> abgeschlosseneTermine = new ArrayList<>();
		for (Terminfindung termin : termine) {
			if (termin.getFrist().compareTo(LocalDateTime.now()) <= 0) {
				abgeschlosseneTermine.add(termin);
			}
		}
		
		// sortieren
		abgeschlosseneTermine = sortiereTermine(abgeschlosseneTermine);
		
		return abgeschlosseneTermine;
	}
	
	private List<Terminfindung> distinct(List<Terminfindung> termine) {
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
		termine = distinct(termine);
		
		return termine;
	}
	
	private List<Terminfindung> sortiereTermine(List<Terminfindung> termine) {
		
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
	
	/**
	 * Lädt alle Termine zu einem Bestimmten Nutzer
	 * @param account
	 * @return Liste von Terminen eines Nutzers
	 */
	private List<Terminfindung> getTermineVonBenutzer(Account account) {
		List<Terminfindung> termine = new ArrayList<>();
		termine.addAll(terminfindungService.loadByErstellerOhneTermine(account.getName()));
		
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		for (Gruppe g : gruppen) {
			termine.addAll(terminfindungService.loadByGruppeOhneTermine(g.getName()));
		}
		
		return termine;
	}
	
}
