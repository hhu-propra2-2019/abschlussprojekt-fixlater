package mops.termine2.services;

import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Modus;
import mops.termine2.models.Umfrage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Der UmfrageService ist die zentrale Schnittstelle zwischen
 * der Datenbank und den Controllern für die Umfragen. Es werden
 * Methoden zum Speichern, Löschen und Aktualisieren einer
 * Umfrage angeboten.
 */
@Service
public class UmfrageService {
	
	private transient UmfrageRepository umfrageRepository;
	
	private transient UmfrageAntwortRepository umfrageAntwortRepository;
	
	public UmfrageService(UmfrageRepository umfrageRepo, UmfrageAntwortRepository antwortRepo) {
		umfrageRepository = umfrageRepo;
		umfrageAntwortRepository = antwortRepo;
	}
	
	/**
	 * Speichert eine neue Umfrage in der Datenbank
	 *
	 * @param umfrage Die Umfrage, 
	 * 		die in der Datenbank gespeichert werden soll
	 */
	@Transactional
	public void save(Umfrage umfrage) {
		String link = umfrage.getLink();
		List<UmfrageDB> alteAuswahl = umfrageRepository.findByLink(link);
		List<UmfrageDB> toSave = new ArrayList<>();
		
		List<String> vorschlaege = new ArrayList<>();
		List<UmfrageDB> toDelete = new ArrayList<>();
		toDelete.addAll(alteAuswahl);
		for (UmfrageDB umfrageDB : alteAuswahl) {
			vorschlaege.add(umfrageDB.getAuswahlmoeglichkeit());
		}
		
		for (String vorschlag : umfrage.getVorschlaege()) {
			UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setTitel(umfrage.getTitel());
			umfrageDB.setErsteller(umfrage.getErsteller());
			umfrageDB.setFrist(umfrage.getFrist());
			umfrageDB.setLoeschdatum(umfrage.getLoeschdatum());
			umfrageDB.setLink(umfrage.getLink());
			umfrageDB.setBeschreibung(umfrage.getBeschreibung());
			umfrageDB.setGruppeId(umfrage.getGruppeId());
			umfrageDB.setAuswahlmoeglichkeit(vorschlag);
			umfrageDB.setErgebnis(umfrage.getErgebnis());
			
			if (umfrage.getGruppeId() != null) {
				umfrageDB.setModus(Modus.GRUPPE);
			} else {
				umfrageDB.setModus(Modus.LINK);
			}
			
			if (vorschlaege.contains(umfrageDB.getAuswahlmoeglichkeit())) {
				int index = vorschlaege.indexOf(umfrageDB.getAuswahlmoeglichkeit());
				UmfrageDB toUpdate = alteAuswahl.get(index);
				updateOldDB(umfrageDB, toUpdate);
				toSave.add(toUpdate);
				toDelete.remove(toUpdate);
				
			} else {
				toSave.add(umfrageDB);
			}
		}
		umfrageRepository.saveAll(toSave);
		umfrageRepository.deleteAll(toDelete);
	}
	
	/**
	 * Löscht eine Umfrage und zugehörige Antworten 
	 * zu dem gegebenen Link
	 *
	 * @param link Der Link, zu dem die Umfrage und Antworten
	 * 		gelöscht werden sollen
	 */
	public void loescheNachLink(String link) {
		umfrageAntwortRepository.deleteAllByUmfrageLink(link);
		umfrageRepository.deleteByLink(link);
	}
	
	/**
	 * Löscht eine Umfrage und zugehörige Antworten 
	 * der gegebenen Gruppe
	 *
	 * @param gruppeId Die Gruppe-ID, deren Umfragen gelöscht werden sollen
	 */
	public void loescheNachGruppe(String gruppeId) {
		umfrageRepository.deleteByGruppeId(gruppeId);
	}
	
	/**
	 * Löscht alle abgelaufene Umfragen und zugehörige Antworten.
	 * Eine abgelaufene Umfrage ist eine Umfrage, deren
	 * Löschdatum in der Vergangenheit liegt
	 */
	@Transactional
	public void loescheAbgelaufeneUmfragen() {
		LocalDateTime now = LocalDateTime.now();
		umfrageAntwortRepository.deleteByUmfrageLoeschdatumBefore(now);
		umfrageRepository.deleteByLoeschdatumBefore(now);
		
	}
	
	/**
	 * Lädt die Umfragen aus der Datenbank mit Link {@code link} aus
	 * der Datenbank. Dabei werden Vorschläge geladen.
	 * 
	 * @param link Der Link der Umfrage, die gesucht werden soll
	 * 
	 * @return Die Umfrage mit Link {@code link} oder {@code null}, falls
	 * 		der Link nicht gefunden wird
	 */
	public Umfrage loadByLink(String link) {
		List<UmfrageDB> umfragenDB = umfrageRepository.findByLink(link);
		if (umfragenDB != null && !umfragenDB.isEmpty()) {
			Umfrage umfrage = new Umfrage();
			UmfrageDB ersteUmfrage = umfragenDB.get(0);
			
			umfrage.setBeschreibung(ersteUmfrage.getBeschreibung());
			umfrage.setErsteller(ersteUmfrage.getErsteller());
			umfrage.setFrist(ersteUmfrage.getFrist());
			umfrage.setGruppeId(ersteUmfrage.getGruppeId());
			umfrage.setLink(ersteUmfrage.getLink());
			umfrage.setLoeschdatum(ersteUmfrage.getLoeschdatum());
			umfrage.setMaxAntwortAnzahl(ersteUmfrage.getMaxAntwortAnzahl());
			umfrage.setTitel(ersteUmfrage.getTitel());
			
			List<String> vorschlaege = new ArrayList<String>();
			for (UmfrageDB umfrageDB : umfragenDB) {
				vorschlaege.add(umfrageDB.getAuswahlmoeglichkeit());
			}
			umfrage.setVorschlaege(vorschlaege);
			return umfrage;
		}
		return null;
	}
	
	/**
	 * Lädt alle Umfragen aus der Datenbank von dem Benutzer {@code ersteller}
	 * und gibt diese zurück. Dabei werden keine Vorschläge geladen. 
	 * 
	 * @param ersteller Der Benutzer dessen Umfragen gesucht werden sollen
	 * 
	 * @return Die Liste der Umfragen, die von dem Benutzer {@code ersteller}
	 * 		erstellt wurden
	 */
	public List<Umfrage> loadByErstellerOhneVorschlaege(String ersteller) {
		List<UmfrageDB> umfrageDBs = umfrageRepository.findByErstellerOrderByFristAsc(ersteller);
		return getEindeutigeUmfragen(umfrageDBs);
	}
	
	/**
	 * Lädt alle Umfragen aus der Datenbank für die Gruppe
	 * mit Gruppen-ID {@code gruppeId} und gibt diese zurück. 
	 * Dabei werden keine Vorschläge geladen. 
	 * 
	 * @param gruppeId Die Gruppe deren Umfragen gesucht werden sollen
	 * 
	 * @return Die Liste der Umfragen, die zu der Gruppe mit Gruppen-ID
	 * 		{@code gruppeId} gehören
	 */
	public List<Umfrage> loadByGruppeOhneVorschlaege(String gruppeId) {
		List<UmfrageDB> umfrageDBs = umfrageRepository.findByGruppeIdOrderByFristAsc(gruppeId);
		return getEindeutigeUmfragen(umfrageDBs);
	}
	
	/**
	 * Lädt alle Umfragen aus der Datenbank, bei denen der Benutzer
	 * {@code benutzer} abgestimmt hat und gibt diese zurück. 
	 * Dabei werden keine Vorschläge geladen. 
	 * 
	 * @param benutzer Der Benutzer dessen Umfragen gesucht werden sollen
	 * 
	 * @return Die Liste der Umfragen, bei denen der Benutzer
	 * 		{@code benutzer} abgestimmt hat
	 */
	public List<Umfrage> loadAllBenutzerHatAbgestimmtOhneVorschlaege(String benutzer) {
		List<UmfrageDB> umfrageDBs = umfrageAntwortRepository.findUmfrageDbByBenutzer(benutzer);
		List<Umfrage> umfragen = getEindeutigeUmfragen(umfrageDBs);
		return umfragen;
	}
	
	/**
	 * Lädt die Umfragen aus der Datenbank mit Link {@code link} aus
	 * der Datenbank. Dabei werden Vorschläge geladen.
	 * 
	 * @param link Der Link der Umfrage, die gesucht werden soll
	 * 
	 * @return Die Umfrage mit Link {@code link} oder {@code null}, falls
	 * 		der Link nicht gefunden wird
	 */
	public Umfrage loadByLinkMitVorschlaegen(String link) {
		List<UmfrageDB> vorschlaegeDB = umfrageRepository.findByLink(link);
		if (vorschlaegeDB != null && !vorschlaegeDB.isEmpty()) {
			Umfrage umfrage = new Umfrage();
			UmfrageDB ersteUmfrage = vorschlaegeDB.get(0);
			
			umfrage.setTitel(ersteUmfrage.getTitel());
			umfrage.setBeschreibung(ersteUmfrage.getBeschreibung());
			umfrage.setLoeschdatum(ersteUmfrage.getLoeschdatum());
			umfrage.setFrist(ersteUmfrage.getFrist());
			umfrage.setGruppeId(ersteUmfrage.getGruppeId());
			umfrage.setLink(ersteUmfrage.getLink());
			umfrage.setErsteller(ersteUmfrage.getErsteller());
			umfrage.setErgebnis(ersteUmfrage.getErgebnis());
			
			List<String> vorschlaege = new ArrayList<>();
			for (UmfrageDB vorschlag : vorschlaegeDB) {
				vorschlaege.add(vorschlag.getAuswahlmoeglichkeit());
			}
			umfrage.setVorschlaege(vorschlaege);
			return umfrage;
		}
		return null;
	}
	
	/**
	 * Übersetzt eine Liste von UmfrageDB Objekten in eine
	 * Liste von Umfrage Objekten. Dabei sind die Umfragen 
	 * nach Link eindeutig.
	 * 
	 * @param umfrageDBs Die Liste von UmfrageDB Objekten, 
	 * die übersetzt werden sollen
	 * 
	 * @return Die Liste von Umfrage Objekten, die aus {@code umfrageDBs}
	 * 		entstand
	 */
	public List<Umfrage> getEindeutigeUmfragen(List<UmfrageDB> umfrageDBs) {
		List<Umfrage> distinctUmfrage = new ArrayList<Umfrage>();
		List<String> links = new ArrayList<String>();
		for (UmfrageDB umfragedb : umfrageDBs) {
			if (!links.contains(umfragedb.getLink())) {
				distinctUmfrage.add(erstelleUmfrageOhneVorschlaege(umfragedb));
				links.add(umfragedb.getLink());
			}
		}
		return distinctUmfrage;
	}
	
	/**
	 * Erstellt ein neues Umfrage Objekt mit einer leeren Vorschlagliste,
	 * Frist eine Woche in der Zukunft und Löschdatum vier Wochen in der Zukunft
	 * 
	 * @return Das erstellte Umfrage Objekt
	 */
	public Umfrage createDefaultUmfrage() {
		Umfrage umfrage = new Umfrage();
		umfrage.setVorschlaege(new ArrayList<>());
		umfrage.getVorschlaege().add("");
		umfrage.setFrist(LocalDateTime.now().plusWeeks(1));
		umfrage.setLoeschdatum(LocalDateTime.now().plusWeeks(4));
		return umfrage;
	}
	
	/**
	 * Löscht den Vorschlag von {@code umfrage} an der Stelle
	 * {@code indexToDelete}. Tritt eine {@link NullPointerException} oder
	 * {@link IndexOutOfBoundsException} auf, so wird nicht gelöscht.
	 * 
	 * @param terminfindung Die Umfrage, in der der Vorschlag gelöscht werden soll
	 * @param indexToDelete Der index des Vorschlags, der gelöscht werden soll
	 */
	public void loescheVorschlag(Umfrage umfrage, int indexToDelete) {
		try {
			umfrage.getVorschlaege().remove(indexToDelete);
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			return;
		}
	}
	
	/**
	 * Aktualisiert eine Umfrage und überprüft die Gültigkeit der Eingaben.
	 * Sind die Eingaben ungültig, wird die entsprechende Fehlermeldung in eine
	 * Liste geschrieben und zurückgegeben. Bei Erfolg ist diese Liste leer.
	 * 
	 * @param account Das Account Objekt des aktuellen Benutzers. Wird als Ersteller eingetragen
	 * @param umfrage Die Umfrage, deren Attribute aktualisiert werden sollen
	 *
	 * @return Die Liste von Fehlermeldungen
	 */
	public List<String> erstelleUmfrage(Account account, Umfrage umfrage) {
		List<String> fehler = new ArrayList<String>();
		
		ArrayList<String> gueltigeVorschlaege = new ArrayList<String>();
		for (String vorschlag : umfrage.getVorschlaege()) {
			if (vorschlag != null && !vorschlag.equals("") && !gueltigeVorschlaege.contains(vorschlag)) {
				gueltigeVorschlaege.add(vorschlag);
			}
		}
		
		if (gueltigeVorschlaege.isEmpty()) {
			gueltigeVorschlaege.add("");
			fehler.add(Konstanten.MESSAGE_KEIN_VORSCHLAG);
		}
		
		umfrage.setVorschlaege(gueltigeVorschlaege);
		umfrage.setMaxAntwortAnzahl((long) gueltigeVorschlaege.size());
		umfrage.setErsteller(account.getName());
		
		return fehler;
	}
	
	/**
	 * Setzt den Gruppennamen in einer Liste von Umfragen in Abhängigkeit von der
	 * Gruppen-ID
	 * 
	 * @param umfragen Die Umfragen, deren Gruppennamen gesetzt werden sollen
	 * @param gruppen Die bekannten Gruppen mit der Gruppen-ID als Schlüssel
	 * 		und Gruppennamen als Wert
	 */
	public void setzeGruppenName(List<Umfrage> umfragen, HashMap<String, String> gruppen) {
		for (Umfrage umfrage : umfragen) {
			umfrage.setGruppeName(gruppen.get(umfrage.getGruppeId()));
		}
	}
	
	private void updateOldDB(UmfrageDB umfrage, UmfrageDB toUpdate) {
		toUpdate.setTitel(umfrage.getTitel());
		toUpdate.setErsteller(umfrage.getErsteller());
		toUpdate.setFrist(umfrage.getFrist());
		toUpdate.setLoeschdatum(umfrage.getLoeschdatum());
		toUpdate.setLink(umfrage.getLink());
		toUpdate.setBeschreibung(umfrage.getBeschreibung());
		toUpdate.setGruppeId(umfrage.getGruppeId());
		toUpdate.setErgebnis(umfrage.getErgebnis());
	}
	
	private Umfrage erstelleUmfrageOhneVorschlaege(UmfrageDB umfragedb) {
		Umfrage umfrage = new Umfrage();
		umfrage.setBeschreibung(umfragedb.getBeschreibung());
		umfrage.setErsteller(umfragedb.getErsteller());
		umfrage.setFrist(umfragedb.getFrist());
		umfrage.setGruppeId(umfragedb.getGruppeId());
		umfrage.setLink(umfragedb.getLink());
		umfrage.setLoeschdatum(umfragedb.getLoeschdatum());
		umfrage.setMaxAntwortAnzahl(umfragedb.getMaxAntwortAnzahl());
		umfrage.setTitel(umfragedb.getTitel());
		umfrage.setVorschlaege(new ArrayList<String>());
		umfrage.setErgebnis(umfragedb.getErgebnis());
		return umfrage;
	}
	
}
