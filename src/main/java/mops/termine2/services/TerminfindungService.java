package mops.termine2.services;

import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.enums.Modus;
import mops.termine2.models.Terminfindung;
import mops.termine2.util.LocalDateTimeManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Der TerminfindungService ist die zentrale Schnittstelle zwischen
 * der Datenbank und den Controllern für die Terminfindung. Es werden
 * Methoden zum Speichern, Löschen und Aktualisieren einer
 * Terminfindung angeboten.
 */
@Service
public class TerminfindungService {
	
	private transient TerminfindungRepository terminfindungRepo;
	
	private transient TerminfindungAntwortRepository antwortRepo;
	
	public TerminfindungService(TerminfindungRepository terminfindungRepo,
								TerminfindungAntwortRepository antwortRepo) {
		this.terminfindungRepo = terminfindungRepo;
		this.antwortRepo = antwortRepo;
	}
	
	/**
	 * Speichert eine neue Terminfindung in der Datenbank
	 *
	 * @param terminfindung Die Terminfindung, 
	 * 		die in der Datenbank gespeichert werden soll
	 */
	@Transactional
	public void save(Terminfindung terminfindung) {
		String link = terminfindung.getLink();
		List<TerminfindungDB> alteTermine = terminfindungRepo.findByLink(link);
		List<TerminfindungDB> toSave = new ArrayList<>();
		
		List<LocalDateTime> dates = new ArrayList<>();
		List<TerminfindungDB> toDelete = new ArrayList<>();
		toDelete.addAll(alteTermine);
		for (TerminfindungDB termin : alteTermine) {
			dates.add(termin.getTermin());
		}
		
		for (LocalDateTime termin : terminfindung.getVorschlaege()) {
			TerminfindungDB terminfindungDB = new TerminfindungDB();
			terminfindungDB.setTitel(terminfindung.getTitel());
			terminfindungDB.setOrt(terminfindung.getOrt());
			terminfindungDB.setErsteller(terminfindung.getErsteller());
			terminfindungDB.setFrist(terminfindung.getFrist());
			terminfindungDB.setLoeschdatum(terminfindung.getLoeschdatum());
			terminfindungDB.setLink(terminfindung.getLink());
			terminfindungDB.setBeschreibung(terminfindung.getBeschreibung());
			terminfindungDB.setGruppeId(terminfindung.getGruppeId());
			terminfindungDB.setTermin(termin);
			terminfindungDB.setErgebnis(terminfindung.getErgebnis());
			terminfindungDB.setErgebnisVorFrist(terminfindung.getErgebnisVorFrist());
			terminfindungDB.setEinmaligeAbstimmung(terminfindung.getEinmaligeAbstimmung());
			
			if (terminfindung.getGruppeId() != null) {
				terminfindungDB.setModus(Modus.GRUPPE);
			} else {
				terminfindungDB.setModus(Modus.LINK);
			}
			
			if (dates.contains(terminfindungDB.getTermin())) {
				int index = dates.indexOf(terminfindungDB.getTermin());
				TerminfindungDB toUpdate = alteTermine.get(index);
				updateOldDB(terminfindungDB, toUpdate);
				toSave.add(toUpdate);
				toDelete.remove(toUpdate);
				
			} else {
				toSave.add(terminfindungDB);
			}
		}
		
		terminfindungRepo.saveAll(toSave);
		terminfindungRepo.deleteAll(toDelete);
	}
	
	/**
	 * Löscht eine Terminfindung und zugehörige Antworten 
	 * zu dem gegebenen Link
	 *
	 * @param link Der Link, zu dem die Terminfindung und Antworten
	 * 		gelöscht werden sollen
	 */
	@Transactional
	public void loescheNachLink(String link) {
		antwortRepo.deleteByTerminfindungLink(link);
		terminfindungRepo.deleteByLink(link);
	}
	
	/**
	 * Löscht alle abgelaufene Terminfindungen und zugehörige Antworten.
	 * Eine abgelaufene Terminfindung ist eine Terminfindung, deren
	 * Löschdatum in der Vergangenheit liegt
	 */
	@Transactional
	public void loescheAbgelaufeneTermine() {
		LocalDateTime timeNow = LocalDateTime.now();
		antwortRepo.deleteByTerminfindungLoeschdatumBefore(timeNow);
		terminfindungRepo.deleteByLoeschdatumBefore(timeNow);
	}
	
	/**
	 * Lädt alle Terminfindungen aus der Datenbank von dem Benutzer {@code ersteller}
	 * und gibt diese zurück. Dabei werden keine Terminvorschläge geladen. 
	 * 
	 * @param ersteller Der Benutzer dessen Terminfindungen gesucht werden sollen
	 * 
	 * @return Die Liste der Terminfindungen, die von dem Benutzer {@code ersteller}
	 * 		erstellt wurden
	 */
	public List<Terminfindung> loadByErstellerOhneTermine(String ersteller) {
		List<TerminfindungDB> terminfindungDBs = terminfindungRepo.findByErstellerOrderByFristAsc(ersteller);
		List<Terminfindung> terminfindungen = getEindeutigeTerminfindungen(terminfindungDBs);
		return terminfindungen;
	}
	
	/**
	 * Lädt alle Terminfindungen aus der Datenbank für die Gruppe
	 * mit Gruppen-ID {@code gruppeId} und gibt diese zurück. 
	 * Dabei werden keine Terminvorschläge geladen. 
	 * 
	 * @param gruppeId Die Gruppe deren Terminfindungen gesucht werden sollen
	 * 
	 * @return Die Liste der Terminfindungen, die zu der Gruppe mit Gruppen-ID
	 * 		{@code gruppeId} gehören
	 */
	public List<Terminfindung> loadByGruppeOhneTermine(String gruppeId) {
		List<TerminfindungDB> terminfindungDBs = terminfindungRepo.findByGruppeIdOrderByFristAsc(gruppeId);
		List<Terminfindung> terminfindungen = getEindeutigeTerminfindungen(terminfindungDBs);
		return terminfindungen;
	}
	
	/**
	 * Lädt alle Terminfindungen aus der Datenbank, bei denen der Benutzer
	 * {@code benutzer} abgestimmt hat und gibt diese zurück. 
	 * Dabei werden keine Terminvorschläge geladen. 
	 * 
	 * @param benutzer Der Benutzer dessen Terminfindungen gesucht werden sollen
	 * 
	 * @return Die Liste der Terminfindungen, bei denen der Benutzer
	 * 		{@code benutzer} abgestimmt hat
	 */
	public List<Terminfindung> loadAllBenutzerHatAbgestimmtOhneTermine(String benutzer) {
		List<TerminfindungDB> terminfindungDBs = antwortRepo.findTerminfindungDbByBenutzer(benutzer);
		List<Terminfindung> terminfindungen = getEindeutigeTerminfindungen(terminfindungDBs);
		return terminfindungen;
	}
	
	/**
	 * Lädt die Terminfindungen aus der Datenbank mit Link {@code link} aus
	 * der Datenbank und schaut, ob der Benutzer bereits teilgenommen hat
	 * oder nicht. Dabei werden Terminvorschläge geladen.
	 * 
	 * @param link Der Link der Terminfindung, die gesucht werden soll
	 * @param benutzer Der Benutzer dessen Abstimmungsstatus geprüft werden soll
	 * 
	 * @return Die Terminfindung mit Link {@code link} oder {@code null}, falls
	 * 		der Link nicht gefunden wird
	 */
	public Terminfindung loadByLinkMitTerminenForBenutzer(String link, String benutzer) {
		Terminfindung terminfindung = loadByLinkMitTerminen(link);
		if (terminfindung != null) {
			terminfindung.setTeilgenommen(
				!antwortRepo.findByBenutzerAndTerminfindungLink(benutzer, link).isEmpty());
		}
		return terminfindung;
	}
	
	/**
	 * Lädt die Terminfindungen aus der Datenbank mit Link {@code link} aus
	 * der Datenbank. Dabei werden Terminvorschläge geladen.
	 * 
	 * @param link Der Link der Terminfindung, die gesucht werden soll
	 * 
	 * @return Die Terminfindung mit Link {@code link} oder {@code null}, falls
	 * 		der Link nicht gefunden wird
	 */
	public Terminfindung loadByLinkMitTerminen(String link) {
		List<TerminfindungDB> termineDB = terminfindungRepo.findByLink(link);
		if (termineDB != null && !termineDB.isEmpty()) {
			Terminfindung terminfindung = new Terminfindung();
			TerminfindungDB ersterTermin = termineDB.get(0);
			
			terminfindung.setTitel(ersterTermin.getTitel());
			terminfindung.setBeschreibung(ersterTermin.getBeschreibung());
			terminfindung.setOrt(ersterTermin.getOrt());
			terminfindung.setLoeschdatum(ersterTermin.getLoeschdatum());
			terminfindung.setFrist(ersterTermin.getFrist());
			terminfindung.setGruppeId(ersterTermin.getGruppeId());
			terminfindung.setLink(ersterTermin.getLink());
			terminfindung.setErsteller(ersterTermin.getErsteller());
			terminfindung.setErgebnis(ersterTermin.getErgebnis());
			terminfindung.setErgebnisVorFrist(ersterTermin.getErgebnisVorFrist());
			terminfindung.setEinmaligeAbstimmung(ersterTermin.getEinmaligeAbstimmung());
			
			List<LocalDateTime> terminMoeglichkeiten = new ArrayList<>();
			for (TerminfindungDB termin : termineDB) {
				terminMoeglichkeiten.add(termin.getTermin());
			}
			terminfindung.setVorschlaege(terminMoeglichkeiten);
			return terminfindung;
		}
		return null;
	}
	
	/**
	 * Übersetzt eine Liste von TerminfindungDB Objekten in eine
	 * Liste von Terminfindung Objekten. Dabei sind die Terminfindungen 
	 * nach Link eindeutig.
	 * 
	 * @param terminfindungDBs Die Liste von TerminfindungDB Objekten, 
	 * die übersetzt werden sollen
	 * 
	 * @return Die Liste von Terminfindung Objekten, die aus {@code terminfindungDBs}
	 * 		entstand
	 */
	public List<Terminfindung> getEindeutigeTerminfindungen(List<TerminfindungDB> terminfindungDBs) {
		List<TerminfindungDB> distinctTerminfindungDBs = new ArrayList<>();
		List<String> links = new ArrayList<>();
		for (TerminfindungDB terminfindungdb : terminfindungDBs) {
			if (!links.contains(terminfindungdb.getLink())) {
				distinctTerminfindungDBs.add(terminfindungdb);
				links.add(terminfindungdb.getLink());
			}
		}
		
		List<Terminfindung> terminfindungen = new ArrayList<>();
		for (TerminfindungDB db : distinctTerminfindungDBs) {
			terminfindungen.add(erstelleTerminfindungOhneTermine(db));
		}
		return terminfindungen;
	}
	
	/**
	 * Erstellt ein neues Terminfindung Objekt mit einer leeren Vorschlagliste,
	 * Frist eine Woche in der Zukunft, Löschdatum vier Wochen in der Zukunft
	 * und dem ErgebnisVorFrist Flag auf {@code true}
	 * 
	 * @return Das erstellte Terminfindung Objekt
	 */
	public Terminfindung createDefaultTerminfindung() {
		Terminfindung terminfindung = new Terminfindung();
		terminfindung.setVorschlaege(new ArrayList<>());
		terminfindung.getVorschlaege().add(null);
		terminfindung.setFrist(LocalDateTime.now().plusWeeks(1));
		terminfindung.setLoeschdatum(LocalDateTime.now().plusWeeks(4));
		terminfindung.setErgebnisVorFrist(true);
		return terminfindung;
	}
	
	/**
	 * Löscht den Terminvorschlag von {@code terminfindung} an der Stelle
	 * {@code indexToDelete}. Tritt eine {@link NullPointerException} oder
	 * {@link IndexOutOfBoundsException} auf, so wird nicht gelöscht.
	 * 
	 * @param terminfindung Die Terminfindung, in der der Termin gelöscht werden soll
	 * @param indexToDelete Der index des Termins, der gelöscht werden soll
	 */
	public void loescheTermin(Terminfindung terminfindung, int indexToDelete) {
		try {
			terminfindung.getVorschlaege().remove(indexToDelete);
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			return;
		}
	}
	
	/**
	 * Aktualisiert eine Terminfindung und überprüft die Gültigkeit der Eingaben.
	 * Sind die Eingaben ungültig, wird die entsprechende Fehlermeldung in eine
	 * Liste geschrieben und zurückgegeben. Bei Erfolg ist diese Liste leer.
	 * 
	 * @param account Das Account Objekt des aktuellen Benutzers. Wird als Ersteller eingetragen
	 * @param terminfindung Die Terminfindung, deren Attribute aktualisiert werden sollen
	 *
	 * @return Die Liste von Fehlermeldungen
	 */
	public List<String> erstelleTerminfindung(Account account, Terminfindung terminfindung) {
		
		List<String> fehler = new ArrayList<String>();
		
		List<LocalDateTime> gueltigeVorschlaege = 
			aktualisiereFristUndLoeschdatum(terminfindung, terminfindung.getVorschlaege());
		
		if (gueltigeVorschlaege.isEmpty()) {
			gueltigeVorschlaege.add(null);
			fehler.add(Konstanten.MESSAGE_KEIN_VORSCHLAG);
		}
		
		if (LocalDateTimeManager.istVergangen(terminfindung.getFrist().minusMinutes(5))) {
			fehler.add(Konstanten.MESSAGE_TERMIN_FRIST_KURZFRISTIG);
		}
		
		terminfindung.setVorschlaege(gueltigeVorschlaege);
		
		// Terminfindung erstellen
		terminfindung.setErsteller(account.getName());		
		return fehler;
	}
	
	/**
	 * Setzt den Gruppennamen in einer Liste von Terminfindungen in Abhängigkeit von der
	 * Gruppen-ID
	 * 
	 * @param terminfindungen Die Terminfindungen, deren Gruppennamen gesetzt werden sollen
	 * @param gruppen Die bekannten Gruppen mit der Gruppen-ID als Schlüssel
	 * 		und Gruppennamen als Wert
	 */
	public void setzeGruppenName(List<Terminfindung> terminfindungen, HashMap<String, String> gruppen) {
		for (Terminfindung terminfindung : terminfindungen) {
			terminfindung.setGruppeName(gruppen.get(terminfindung.getGruppeId()));
		}
	}
	
	/**
	 * Aktualisiert die Frist und das Löschdatum der Terminfindung {@code terminfindung}
	 * in Abhängigkeit von den Terminen in {@code neueTermine}. Ungültige oder
	 * doppelte Einträge werden direkt gefiltert und die Frist wird
	 * möglichst vor dem ersten Vorschlag gesetzt. Das Löschdatum wird vier Wochen
	 * nach dem letzten Vorschlag gesetzt. Werden keine gültigen Vorschläge übergeben,
	 * werden Frist und Löschdatum nicht aktualisiert
	 * 
	 * @param terminfindung Die Terminfindung deren Attribute aktualisiert werden sollen
	 * @param neueTermine Die neuen Termine, von denen Frist und Löschdatum abhängig sind.
	 * 
	 * @return Die Liste der gültigen Vorschläge. Ist leer, wenn keine Vorschläge gültig sind
	 */
	public List<LocalDateTime> aktualisiereFristUndLoeschdatum(Terminfindung terminfindung, 
		List<LocalDateTime> neueTermine) {
		ArrayList<LocalDateTime> gueltigeVorschlaege = LocalDateTimeManager
			.filterUngueltigeDaten(neueTermine);
		LocalDateTime minVorschlag = LocalDateTimeManager
			.bekommeFruehestesDatum(gueltigeVorschlaege);
		LocalDateTime maxVorschlag = LocalDateTimeManager
			.bekommeSpaetestesDatum(gueltigeVorschlaege);
		
		if (minVorschlag != null) {
			setzeFrist(terminfindung, minVorschlag);
			setzeLoeschdatum(terminfindung, maxVorschlag);
		}
		return gueltigeVorschlaege;
	}
	
	private void setzeFrist(Terminfindung terminfindung, LocalDateTime minVorschlag) {
		if (terminfindung.getFrist().isAfter(minVorschlag)) {
			if (minVorschlag.minusDays(1).isAfter(LocalDateTime.now())) {
				terminfindung.setFrist(minVorschlag.minusDays(1));
			} else if (minVorschlag.minusHours(2).isAfter(LocalDateTime.now())) {
				terminfindung.setFrist(minVorschlag.minusHours(2));
			} else if (minVorschlag.minusMinutes(5).isAfter(LocalDateTime.now())) {
				terminfindung.setFrist(minVorschlag.minusMinutes(5));
			} else {
				terminfindung.setFrist(minVorschlag);
			}
		}
	}
	
	private void setzeLoeschdatum(Terminfindung terminfindung, LocalDateTime maxVorschlag) {
		if (terminfindung.getLoeschdatum().isBefore(maxVorschlag)) {
			terminfindung.setLoeschdatum(maxVorschlag.plusWeeks(4));
		}
	}
	
	private void updateOldDB(TerminfindungDB terminfindung, TerminfindungDB toUpdate) {
		toUpdate.setTitel(terminfindung.getTitel());
		toUpdate.setOrt(terminfindung.getOrt());
		toUpdate.setErsteller(terminfindung.getErsteller());
		toUpdate.setFrist(terminfindung.getFrist());
		toUpdate.setLoeschdatum(terminfindung.getLoeschdatum());
		toUpdate.setLink(terminfindung.getLink());
		toUpdate.setBeschreibung(terminfindung.getBeschreibung());
		toUpdate.setGruppeId(terminfindung.getGruppeId());
		toUpdate.setTermin(terminfindung.getTermin());
		toUpdate.setErgebnis(terminfindung.getErgebnis());
		toUpdate.setErgebnisVorFrist(terminfindung.getErgebnisVorFrist());
		toUpdate.setEinmaligeAbstimmung(terminfindung.getEinmaligeAbstimmung());
	}
	
	private Terminfindung erstelleTerminfindungOhneTermine(TerminfindungDB db) {
		Terminfindung terminfindung = new Terminfindung();
		terminfindung.setLink(db.getLink());
		terminfindung.setTitel(db.getTitel());
		terminfindung.setErsteller(db.getErsteller());
		terminfindung.setLoeschdatum(db.getLoeschdatum());
		terminfindung.setFrist(db.getFrist());
		terminfindung.setGruppeId(db.getGruppeId());
		terminfindung.setBeschreibung(db.getBeschreibung());
		terminfindung.setOrt(db.getOrt());
		terminfindung.setErgebnis(db.getErgebnis());
		terminfindung.setErgebnisVorFrist(db.getErgebnisVorFrist());
		terminfindung.setEinmaligeAbstimmung(db.getEinmaligeAbstimmung());
		
		return terminfindung;
	}
	
}
