package mops.termine2.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Modus;
import mops.termine2.models.Umfrage;

@Service
public class UmfrageService {
	
	private transient UmfrageRepository umfrageRepository;
	
	private transient UmfrageAntwortRepository umfrageAntwortRepository;
	
	public UmfrageService(UmfrageRepository umfrageRepo, UmfrageAntwortRepository antwortRepository) {
		umfrageRepository = umfrageRepo;
		umfrageAntwortRepository = antwortRepository;
	}
	
	public void save(Umfrage umfrage) {
		for (String vorschlag : umfrage.getVorschlaege()) {
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
			
			if (umfrage.getGruppe() != null) {
				umfrageDB.setModus(Modus.GRUPPE);
			} else {
				umfrageDB.setModus(Modus.LINK);
			}
			
			umfrageRepository.save(umfrageDB);
		}
	}
	
	public void deleteByLink(String link) {
		umfrageAntwortRepository.deleteAllByUmfrageLink(link);
		umfrageRepository.deleteByLink(link);
	}
	
	public void deleteByGruppe(String gruppe) {
		umfrageRepository.deleteByGruppe(gruppe);
	}
	
	public void deleteOutdated() {
		LocalDateTime now = LocalDateTime.now();
		umfrageRepository.deleteOutdated(now);
		umfrageAntwortRepository.deleteOutdated(now);
	}
	
	public Umfrage loadByLink(String link) {
		List<UmfrageDB> umfragenDB = umfrageRepository.findByLink(link);
		if (umfragenDB != null && !umfragenDB.isEmpty()) {
			Umfrage umfrage = new Umfrage();
			UmfrageDB ersteUmfrage = umfragenDB.get(0);
			
			umfrage.setBeschreibung(ersteUmfrage.getBeschreibung());
			umfrage.setErsteller(ersteUmfrage.getErsteller());
			umfrage.setFrist(ersteUmfrage.getFrist());
			umfrage.setGruppe(ersteUmfrage.getGruppe());
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
		List<UmfrageDB> umfrageDBs = umfrageRepository.findByErsteller(ersteller);
		return getDistinctUmfragen(umfrageDBs);
	}
	
	public List<Umfrage> loadByGruppeOhneUmfragen(String gruppe) {
		List<UmfrageDB> umfrageDBs = umfrageRepository.findByGruppe(gruppe);
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
	
	private Umfrage erstelleUmfrageOhneVorschlaege(UmfrageDB umfragedb) {
		Umfrage umfrage = new Umfrage();
		umfrage.setBeschreibung(umfragedb.getBeschreibung());
		umfrage.setErsteller(umfragedb.getErsteller());
		umfrage.setFrist(umfragedb.getFrist());
		umfrage.setGruppe(umfragedb.getGruppe());
		umfrage.setLink(umfragedb.getLink());
		umfrage.setLoeschdatum(umfragedb.getLoeschdatum());
		umfrage.setMaxAntwortAnzahl(umfragedb.getMaxAntwortAnzahl());
		umfrage.setTitel(umfragedb.getTitel());
		umfrage.setVorschlaege(new ArrayList<String>());
		return umfrage;
	}
	
}
