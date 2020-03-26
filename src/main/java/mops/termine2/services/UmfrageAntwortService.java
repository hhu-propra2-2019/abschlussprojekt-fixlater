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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
	 * Speichert Antworten zu einer Umfragenabstimmung
	 *
	 * @param antwort
	 * @param umfrage
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
	
	public boolean hatNutzerAbgestimmt(String benutzer, String link) {
		List<UmfrageAntwortDB> antworten =
			antwortRepo.findByBenutzerAndUmfrageLink(benutzer,
				link);
		return !antworten.isEmpty();
	}
	
	/**
	 * Lädt eine Liste von Antworten nach Benutzer und Link
	 *
	 * @param benutzer
	 * @param link
	 * @returngibt eine Antwort zu einer Umfrage
	 */
	public UmfrageAntwort loadByBenutzerAndLink(String benutzer, String link) {
		List<UmfrageAntwortDB> alteAntwort = antwortRepo.findByBenutzerAndUmfrageLink(benutzer, link);
		List<UmfrageDB> antwortMoeglichkeiten = umfrageRepo.findByLink(link);
		return buildAntwortForBenutzer(benutzer, alteAntwort, antwortMoeglichkeiten);
	}
	
	/**
	 * Lädt alle Antworten die zu einem Link gehören
	 *
	 * @param link
	 * @return eine Liste von Antworten
	 */
	
	public List<UmfrageAntwort> loadAllByLink(String link) {
		List<UmfrageAntwortDB> antwortDBList =
			antwortRepo.findAllByUmfrageLink(link);
		List<UmfrageDB> antwortMoeglichkeiten = umfrageRepo.findByLink(link);
		
		return buildAntworten(antwortDBList,
			antwortMoeglichkeiten);
	}
	
	private List<UmfrageAntwort> buildAntworten(
		List<UmfrageAntwortDB> antwortDBS, List<UmfrageDB> antwortMoeglichkeiten) {
		
		List<UmfrageAntwort> umfrageAntworten = new ArrayList<>();
		if (!antwortDBS.isEmpty()) {
			List<String> benutzernamen = new ArrayList<>();
			
			for (UmfrageAntwortDB antwortDB : antwortDBS) {
				String benutzer = antwortDB.getBenutzer();
				if (!benutzernamen.contains(antwortDB.getBenutzer())) {
					List<UmfrageAntwortDB> nutzerAntworten = filterAntwortenDbBenutzer(
						antwortDBS, benutzer);
					umfrageAntworten.add(buildAntwortForBenutzer(
						benutzer, nutzerAntworten, antwortMoeglichkeiten));
					benutzernamen.add(benutzer);
				}
				
			}
		}
		return umfrageAntworten;
		
	}
	
	
	private UmfrageAntwort buildAntwortForBenutzer(
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
		
		HashMap<String, Antwort> alteAntwortenMap = new HashMap<>();
		for (UmfrageAntwortDB alteAntwort : alteAntworten) {
			alteAntwortenMap.put(alteAntwort.getUmfrage().getAuswahlmoeglichkeit(),
				alteAntwort.getAntwort());
		}
		HashMap<String, Antwort> antwortenMap = new HashMap<>();
		
		for (UmfrageDB antwortMoglichkeit : antwortMoglichkeiten) {
			String vorschalg = antwortMoglichkeit.getAuswahlmoeglichkeit();
			Antwort alteAntwort = alteAntwortenMap.get(vorschalg);
			antwortenMap.put(vorschalg, Objects.requireNonNullElse(alteAntwort, Antwort.NEIN));
		}
		
		antwort.setAntworten(antwortenMap);
		return antwort;
	}
	
	private List<UmfrageAntwortDB> filterAntwortenDbBenutzer(
		List<UmfrageAntwortDB> antwortDBS, String benutzer) {
		List<UmfrageAntwortDB> nutzerAntwortenDB = new ArrayList<>();
		for (UmfrageAntwortDB umfrageAntwortDB : antwortDBS) {
			if (benutzer.equals(umfrageAntwortDB.getBenutzer())) {
				nutzerAntwortenDB.add(umfrageAntwortDB);
			}
		}
		return nutzerAntwortenDB;
	}
	
	/**
	 * Löscht alle Antworten nach Link
	 *
	 * @param link
	 */
}
