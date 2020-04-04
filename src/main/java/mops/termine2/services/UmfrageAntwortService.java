package mops.termine2.services;

import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Umfrage;
import mops.termine2.models.UmfrageAntwort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Der UmfrageAntwortService bietet Methoden zur Abstimmung in einer Umfrage
 * und bildet die dementsprechende Schnittstelle zwischen Datenbank und Controller
 */
@Service
public class UmfrageAntwortService {
	
	@Autowired
	private UmfrageAntwortRepository antwortRepo;
	
	@Autowired
	private UmfrageRepository umfrageRepo;
	
	public UmfrageAntwortService(UmfrageAntwortRepository umfrageAntwortRepository,
								 UmfrageRepository umfrageRepository) {
		this.antwortRepo = umfrageAntwortRepository;
		this.umfrageRepo = umfrageRepository;
	}
	
	/**
	 * Speichert Antworten zu einer Umfrage in der Datenbank
	 *
	 * @param antwort Die Antwort des Benutzers für die Abstimmung 
	 * @param terminfindung Die Umfrage, bei der abgestimmt wurde
	 */
	public void abstimmen(UmfrageAntwort antwort, Umfrage umfrage) {
		
		List<UmfrageAntwortDB> antwortenToDelete =
			antwortRepo.findByBenutzerAndUmfrageLink(antwort.getBenutzer(),
				umfrage.getLink());
		
		antwortRepo.deleteAll(antwortenToDelete);
		
		for (String vorschlag : antwort.getAntworten().keySet()) {
			UmfrageAntwortDB umfrageAntwortDB = new UmfrageAntwortDB();
			UmfrageDB umfrageDB = umfrageRepo
				.findByLinkAndAuswahlmoeglichkeit(umfrage.getLink(), vorschlag);
			
			umfrageAntwortDB.setAntwort(antwort.getAntworten().get(vorschlag));
			umfrageAntwortDB.setBenutzer(antwort.getBenutzer());
			umfrageAntwortDB.setPseudonym(antwort.getPseudonym());
			umfrageAntwortDB.setUmfrage(umfrageDB);
			
			if (umfrageDB != null) {
				antwortRepo.save(umfrageAntwortDB);
			}
		}
	}
	
	/**
	 * Löscht alle Antworten der Umfrage mit Link {@code link}
	 *
	 * @param link Der Link zu der Umfrage
	 */
	public void deleteAllByLink(String link) {
		antwortRepo.deleteAllByUmfrageLink(link);
	}
	
	/**
	 * Prüft, ob der Benutzer bei der entsprechenden Umfrage abgestimmt hat
	 * 
	 * @param benutzer Der Benutzer, dessen Abstimmungsstatus abgefragt werden soll
	 * @param link Der Link zu der Umfrage
	 * 
	 * @return {@code true}, falls der Benutzer bereits bei der Umfrage abgestimmt hat, 
	 * ansonsten {@code false}
	 */
	public boolean hatNutzerAbgestimmt(String benutzer, String link) {
		List<UmfrageAntwortDB> antworten =
			antwortRepo.findByBenutzerAndUmfrageLink(benutzer, link);
		return !antworten.isEmpty();
	}
	
	/**
	 * Lädt die Antwort eines Benutzers {@code benutzer} zu einer Umfrage
	 * mit Link {@code link}
	 *
	 * @param benutzer der Benutzer, dessen Antwort gesucht wird
	 * @param link der Link der Umfrage
	 * 
	 * @return die Antwort des Benutzers für die Umfrage
	 */	
	public UmfrageAntwort loadByBenutzerUndLink(String benutzer, String link) {
		List<UmfrageAntwortDB> alteAntwort = antwortRepo.findByBenutzerAndUmfrageLink(benutzer, link);
		List<UmfrageDB> antwortMoeglichkeiten = umfrageRepo.findByLink(link);
		return baueAntwortFuerBenutzer(benutzer, alteAntwort, antwortMoeglichkeiten);
	}
	
	/**
	 * Lädt alle Antworten, die zu der Umfrage mit Link {@code link} gehören
	 *
	 * @param link der Link der Umfrage
	 * 
	 * @return Liste von Antworten zu der Umfrage
	 */
	public List<UmfrageAntwort> loadAllByLink(String link) {
		List<UmfrageAntwortDB> antwortDBList =
			antwortRepo.findAllByUmfrageLink(link);
		List<UmfrageDB> antwortMoeglichkeiten = umfrageRepo.findByLink(link);
		
		return baueAntworten(antwortDBList,
			antwortMoeglichkeiten);
	}
	
	private List<UmfrageAntwort> baueAntworten(
		List<UmfrageAntwortDB> antwortDBS, List<UmfrageDB> antwortMoeglichkeiten) {
		
		List<UmfrageAntwort> umfrageAntworten = new ArrayList<>();
		if (!antwortDBS.isEmpty()) {
			List<String> benutzernamen = new ArrayList<>();
			
			for (UmfrageAntwortDB antwortDB : antwortDBS) {
				String benutzer = antwortDB.getBenutzer();
				if (!benutzernamen.contains(antwortDB.getBenutzer())) {
					List<UmfrageAntwortDB> nutzerAntworten = filtereAntwortenNachBenutzer(
						antwortDBS, benutzer);
					umfrageAntworten.add(baueAntwortFuerBenutzer(
						benutzer, nutzerAntworten, antwortMoeglichkeiten));
					benutzernamen.add(benutzer);
				}				
			}
		}
		return umfrageAntworten;		
	}	
	
	private UmfrageAntwort baueAntwortFuerBenutzer(
		String benutzer, List<UmfrageAntwortDB> alteAntworten,
		List<UmfrageDB> antwortMoglichkeiten) {
		
		UmfrageAntwort antwort = new UmfrageAntwort();
		antwort.setBenutzer(benutzer);
		antwort.setLink(antwortMoglichkeiten.get(0).getLink());
		if (!alteAntworten.isEmpty()) {
			antwort.setPseudonym(alteAntworten.get(0).getPseudonym());
		} else {
			antwort.setPseudonym(benutzer);
		}
		
		LinkedHashMap<String, Antwort> alteAntwortenMap = new LinkedHashMap<>();
		for (UmfrageAntwortDB alteAntwort : alteAntworten) {
			alteAntwortenMap.put(alteAntwort.getUmfrage().getAuswahlmoeglichkeit(),
				alteAntwort.getAntwort());
		}
		LinkedHashMap<String, Antwort> antwortenMap = new LinkedHashMap<>();
		
		for (UmfrageDB antwortMoglichkeit : antwortMoglichkeiten) {
			String vorschalg = antwortMoglichkeit.getAuswahlmoeglichkeit();
			Antwort alteAntwort = alteAntwortenMap.get(vorschalg);
			antwortenMap.put(vorschalg, Objects.requireNonNullElse(alteAntwort, Antwort.NEIN));
		}
		
		antwort.setAntworten(antwortenMap);
		return antwort;
	}
	
	private List<UmfrageAntwortDB> filtereAntwortenNachBenutzer(
		List<UmfrageAntwortDB> antwortDBS, String benutzer) {
		List<UmfrageAntwortDB> nutzerAntwortenDB = new ArrayList<>();
		for (UmfrageAntwortDB umfrageAntwortDB : antwortDBS) {
			if (benutzer.equals(umfrageAntwortDB.getBenutzer())) {
				nutzerAntwortenDB.add(umfrageAntwortDB);
			}
		}
		return nutzerAntwortenDB;
	}
	
}
