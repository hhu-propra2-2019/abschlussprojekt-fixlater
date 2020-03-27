package mops.termine2.services;

import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Modus;
import mops.termine2.models.Umfrage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UmfrageService {
	
	private transient UmfrageRepository umfrageRepository;
	
	private transient UmfrageAntwortRepository umfrageAntwortRepository;
	
	public UmfrageService(UmfrageRepository umfrageRepo, UmfrageAntwortRepository antwortRepo) {
		umfrageRepository = umfrageRepo;
		umfrageAntwortRepository = antwortRepo;
	}
	
	/**
	 * Speichert eine neue Umfrage in der DB
	 *
	 * @param umfrage
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
	 * Löscht eine Umfrage und zugehörige Antworten nach Link
	 *
	 * @param link
	 */
	public void deleteByLink(String link) {
		umfrageAntwortRepository.deleteAllByUmfrageLink(link);
		umfrageRepository.deleteByLink(link);
	}
	
	/**
	 * Löscht eine abgelaufene Umfrage und zugehörige Antworten
	 *
	 * @param gruppeId
	 */
	public void deleteByGruppe(Long gruppeId) {
		umfrageRepository.deleteByGruppeId(gruppeId);
	}
	
	@Transactional
	public void loescheAbgelaufeneUmfragen() {
		LocalDateTime now = LocalDateTime.now();
		umfrageAntwortRepository.deleteByUmfrageLoeschdatumBefore(now);
		umfrageRepository.deleteByLoeschdatumBefore(now);
		
	}
	
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
	
	public List<Umfrage> loadByErstellerOhneUmfragen(String ersteller) {
		List<UmfrageDB> umfrageDBs = umfrageRepository.findByErstellerOrderByFristAsc(ersteller);
		return getDistinctUmfragen(umfrageDBs);
	}
	
	public List<Umfrage> loadByGruppeOhneUmfragen(Long gruppeId) {
		List<UmfrageDB> umfrageDBs = umfrageRepository.findByGruppeIdOrderByFristAsc(gruppeId);
		return getDistinctUmfragen(umfrageDBs);
	}
	
	public List<Umfrage> loadAllBenutzerHatAbgestimmtOhneVorschlaege(String benutzer) {
		List<UmfrageDB> umfrageDBs = umfrageAntwortRepository.findUmfrageDbByBenutzer(benutzer);
		List<Umfrage> umfragen = getDistinctUmfragen(umfrageDBs);
		return umfragen;
	}
	
	public List<Umfrage> getDistinctUmfragen(List<UmfrageDB> umfrageDBs) {
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
	
	public List<Umfrage> loadAllBenutzerHatAbgestimmtOhneUmfrage(String benutzer) {
		List<UmfrageDB> umfragenDB = umfrageAntwortRepository.findUmfrageDbByBenutzer(benutzer);
		List<Umfrage> umfragen = getDistinctUmfrageList(umfragenDB);
		
		return umfragen;
	}
	
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
	
	private List<Umfrage> getDistinctUmfrageList(List<UmfrageDB> umfrageDB) {
		List<UmfrageDB> distinctUmfrageDB = new ArrayList<>();
		List<String> links = new ArrayList<>();
		for (UmfrageDB umfragen : umfrageDB) {
			if (!links.contains(umfragen.getLink())) {
				distinctUmfrageDB.add(umfragen);
				links.add(umfragen.getLink());
			}
		}
		
		List<Umfrage> umfragen = new ArrayList<>();
		for (UmfrageDB db : distinctUmfrageDB) {
			umfragen.add(erstelleUmfrageOhneVorschlaege(db));
		}
		
		return umfragen;
	}
	
}
