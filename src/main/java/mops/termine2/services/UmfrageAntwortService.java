package mops.termine2.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Antwort;
import mops.termine2.enums.Modus;
import mops.termine2.models.Umfrage;
import mops.termine2.models.UmfrageAntwort;

@Service
public class UmfrageAntwortService {
	
	private UmfrageAntwortRepository antwortRepo;
	
	public UmfrageAntwortService(UmfrageAntwortRepository umfrageAntwortRepository) {
		antwortRepo = umfrageAntwortRepository;
	}
	
	/**
	 * Speichert Antworten zu einer Umfragenabstimmung
	 * @param antwort
	 * @param umfrage
	 */
	public void abstimmen(UmfrageAntwort antwort, Umfrage umfrage) {
		
		antwortRepo.deleteAllByUmfrageLinkAndBenutzer(umfrage.getLink(), antwort.getBenutzer());
		
		for (String vorschlag : antwort.getAntworten().keySet()) {
			UmfrageAntwortDB umfrageAntwortDB = new UmfrageAntwortDB();
			UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setAuswahlmoeglichkeit(vorschlag);
			umfrageDB.setBeschreibung(umfrage.getBeschreibung());
			umfrageDB.setErsteller(umfrage.getErsteller());
			umfrageDB.setFrist(umfrage.getFrist());
			umfrageDB.setGruppe(umfrage.getGruppe());
			umfrageDB.setLink(umfrage.getLink());
			umfrageDB.setLoeschdatum(umfrage.getLoeschdatum());
			umfrageDB.setMaxAntwortAnzahl(umfrage.getMaxAntwortAnzahl());
			umfrageDB.setTitel(umfrage.getTitel());
			if (umfrage.getGruppe() == null) {
				umfrageDB.setModus(Modus.LINK);
			} else {
				umfrageDB.setModus(Modus.GRUPPE);
			}
			
			umfrageAntwortDB.setAntwort(antwort.getAntworten().get(vorschlag));
			umfrageAntwortDB.setBenutzer(antwort.getBenutzer());
			umfrageAntwortDB.setPseudonym(antwort.getPseudonym());
			umfrageAntwortDB.setUmfrage(umfrageDB);
			
			antwortRepo.save(umfrageAntwortDB);
		}
	}
	
	/**
	 * Lädt eine Liste von Antworten nach Benutzer und Link
	 * @param benutzer
	 * @param link
	 * @returngibt eine Antwort zu einer Umfrage
	 */
	public UmfrageAntwort loadByBenutzerAndLink(String benutzer, String link) {
		List<UmfrageAntwortDB> umfrageAntwortDBs = antwortRepo.findByBenutzerAndUmfrageLink(benutzer, link);
		return buildAntwortFromDB(umfrageAntwortDBs);
	}
	
	/**
	 * Lädt alle Antworten die zu einem Link gehören
	 * @param link
	 * @return eine Liste von Antworten
	 */
	public List<UmfrageAntwort> loadAllByLink(String link) {
		List<UmfrageAntwortDB> umfrageAntwortDBs = antwortRepo.findAllByUmfrageLink(link);
		return buildAntwortenFromDB(umfrageAntwortDBs);
	}
	
	/**
	 * Löscht alle Antworten nach Link
	 * @param link
	 */
	public void deleteAllByLink(String link) {
		antwortRepo.deleteAllByUmfrageLink(link);
	}
	
	private UmfrageAntwort buildAntwortFromDB(List<UmfrageAntwortDB> umfrageAntwortDBs) {
		if (umfrageAntwortDBs != null && !umfrageAntwortDBs.isEmpty()) {
			UmfrageAntwortDB ersteAntwortDB = umfrageAntwortDBs.get(0);
			UmfrageAntwort antwort = new UmfrageAntwort();
			antwort.setBenutzer(ersteAntwortDB.getBenutzer());
			antwort.setGruppe(ersteAntwortDB.getUmfrage().getGruppe());
			antwort.setLink(ersteAntwortDB.getUmfrage().getLink());
			antwort.setPseudonym(ersteAntwortDB.getPseudonym());
			antwort.setTeilgenommen(true);
			
			HashMap<String, Antwort> antworten = new HashMap<>();
			for (UmfrageAntwortDB antwortDB : umfrageAntwortDBs) {
				antworten.put(antwortDB.getUmfrage().getAuswahlmoeglichkeit(), antwortDB.getAntwort());
			}
			antwort.setAntworten(antworten);
			
			return antwort;
		}
		return null;
	}
	
	private List<UmfrageAntwort> buildAntwortenFromDB(List<UmfrageAntwortDB> umfrageAntwortDBs) {
		if (umfrageAntwortDBs != null && !umfrageAntwortDBs.isEmpty()) {
			List<String> benutzernamen = new ArrayList<>();
			List<UmfrageAntwort> umfrageAntworten = new ArrayList<>();
			
			for (UmfrageAntwortDB aktuelleAntwortDB : umfrageAntwortDBs) {
				String aktuellerBenutzer = aktuelleAntwortDB.getBenutzer();
				if (!benutzernamen.contains(aktuellerBenutzer)) {
					UmfrageAntwort antwort = new UmfrageAntwort();
					antwort.setBenutzer(aktuellerBenutzer);
					antwort.setGruppe(aktuelleAntwortDB.getUmfrage().getGruppe());
					antwort.setLink(aktuelleAntwortDB.getUmfrage().getLink());
					antwort.setPseudonym(aktuelleAntwortDB.getPseudonym());
					antwort.setTeilgenommen(true);
					
					HashMap<String, Antwort> antworten = new HashMap<>();
					for (UmfrageAntwortDB db : umfrageAntwortDBs) {
						if (db.getBenutzer().equals(aktuellerBenutzer)) {
							antworten.put(db.getUmfrage().getAuswahlmoeglichkeit(),
								db.getAntwort());
						}
					}
					antwort.setAntworten(antworten);
					
					umfrageAntworten.add(antwort);
					benutzernamen.add(aktuellerBenutzer);
				}
			}
			return umfrageAntworten;
		}
		return null;
	}
	
}
