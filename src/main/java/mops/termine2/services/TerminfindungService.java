package mops.termine2.services;

import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.enums.Modus;
import mops.termine2.models.Terminfindung;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
	 * Speichert eine neue Terminfindung in der DB
	 *
	 * @param terminfindung
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
	 * Löscht eine Terminfindung und zugehörige Antworten nach Link
	 *
	 * @param link
	 */
	@Transactional
	public void loescheByLink(String link) {
		antwortRepo.deleteByTerminfindungLink(link);
		terminfindungRepo.deleteByLink(link);
	}
	
	/**
	 * Löscht eine abgelaufene Terminfindung und zugehörige Antworten
	 */
	@Transactional
	public void loescheAbgelaufeneTermine() {
		LocalDateTime timeNow = LocalDateTime.now();
		antwortRepo.deleteByTerminfindungLoeschdatumBefore(timeNow);
		terminfindungRepo.deleteByLoeschdatumBefore(timeNow);
	}
	
	public List<Terminfindung> loadByErstellerOhneTermine(String ersteller) {
		List<TerminfindungDB> terminfindungDBs = terminfindungRepo.findByErstellerOrderByFristAsc(ersteller);
		List<Terminfindung> terminfindungen = getDistinctTerminfindungList(terminfindungDBs);
		return terminfindungen;
	}
	
	public List<Terminfindung> loadByGruppeOhneTermine(Long gruppeId) {
		List<TerminfindungDB> terminfindungDBs = terminfindungRepo.findByGruppeIdOrderByFristAsc(gruppeId);
		List<Terminfindung> terminfindungen = getDistinctTerminfindungList(terminfindungDBs);
		return terminfindungen;
	}
	
	public List<Terminfindung> loadAllBenutzerHatAbgestimmtOhneTermine(String benutzer) {
		List<TerminfindungDB> terminfindungDBs = antwortRepo.findTerminfindungDbByBenutzer(benutzer);
		List<Terminfindung> terminfindungen = getDistinctTerminfindungList(terminfindungDBs);
		return terminfindungen;
	}
	
	public Terminfindung loadByLinkMitTerminenForBenutzer(String link, String benutzer) {
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
			
			terminfindung.setTeilgenommen(
				!antwortRepo.findByBenutzerAndTerminfindungLink(benutzer, link).isEmpty());
			return terminfindung;
		}
		return null;
	}
	
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
	
	public List<Terminfindung> getDistinctTerminfindungList(List<TerminfindungDB> terminfindungDBs) {
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
