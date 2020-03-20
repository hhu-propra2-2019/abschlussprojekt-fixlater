package mops.termine2.services;

import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
		
		for (Terminfindung termin : termine) {
			if (termin.getFrist().compareTo(LocalDateTime.now()) <= 0) {
				abgeschlosseneTermine.add(termin);
			}
		}
		
		return abgeschlosseneTermine;
	}
	
	/**
	 * Geht die Termine durch und filtert nach offenen die zu einem Nutzer gehören
	 * @param account
	 * @return eine Liste von offenen Terminabstimmungen nach Nutzer
	 */
	public List<Terminfindung> loadOffeneTerminfindungenFuerBenutzer(Account account) {
		List<Terminfindung> termine = getTermineVonBenutzer(account);
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
		List<Terminfindung> termine = getTermineVonBenutzer(account);
		List<Terminfindung> abgeschlosseneTermine = new ArrayList<>();
		
		for (Terminfindung termin : termine) {
			if (termin.getFrist().compareTo(LocalDateTime.now()) <= 0) {
				abgeschlosseneTermine.add(termin);
			}
		}
		
		return abgeschlosseneTermine;
	}
	
	/**
	 * Lädt alle Termine zu einem Bestimmten Nutzer
	 * @param account
	 * @return Liste von Terminen eines Nutzers
	 */
	private List<Terminfindung> getTermineVonBenutzer(Account account) {
		List<Terminfindung> termine = new ArrayList<>();
		
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		
		for (Gruppe g : gruppen) {
			termine.addAll(terminfindungService.loadByGruppeOhneTermine(g.getName()));
		}
		
		return termine;
	}
	
}
