package mops.termine2.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Modus;
import mops.termine2.models.Umfrage;

@Service
public class UmfrageService {
	
	private transient UmfrageRepository umfrageRepository;
	
	public UmfrageService(UmfrageRepository repo) {
		umfrageRepository = repo;
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
		umfrageRepository.deleteByLink(link);
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
	
	public List<Umfrage> loadByErsteller(String ersteller) {
		List<UmfrageDB> umfrageDBs = umfrageRepository.findByErsteller(ersteller);
		return findUmfragenByLink(umfrageDBs);
	}
	
	public List<Umfrage> loadByGruppe(String gruppe) {
		List<UmfrageDB> umfrageDBs = umfrageRepository.findByGruppe(gruppe);
		return findUmfragenByLink(umfrageDBs);
	}
	
	public List<Umfrage> findUmfragenByLink(List<UmfrageDB> umfrageDBs) {
		if (umfrageDBs != null && !umfrageDBs.isEmpty()) {
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
		return null;
	}
	
	private Umfrage erstelleUmfrageOhneVorschlaege(UmfrageDB umfragedb) {
		if (umfragedb != null) {
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
		return null;
	}
	
}