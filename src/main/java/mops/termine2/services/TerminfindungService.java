package mops.termine2.services;

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
	
	public List<Terminfindung> loadByGruppeOhneTermine(String gruppeId) {
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
	
	public Terminfindung createDefaultTerminfindung() {
		Terminfindung terminfindung = new Terminfindung();
		terminfindung.setVorschlaege(new ArrayList<>());
		terminfindung.getVorschlaege().add(null);
		terminfindung.setFrist(LocalDateTime.now().plusWeeks(1));
		terminfindung.setLoeschdatum(LocalDateTime.now().plusWeeks(4));
		terminfindung.setErgebnisVorFrist(true);
		return terminfindung;
	}
	
	public void setzeFrist(Terminfindung terminfindung, LocalDateTime minVorschlag) {
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
	
	public void setzeLoeschdatum(Terminfindung terminfindung, LocalDateTime maxVorschlag) {
		if (terminfindung.getLoeschdatum().isBefore(maxVorschlag)) {
			terminfindung.setLoeschdatum(maxVorschlag.plusWeeks(4));
		}
	}
	
	public void loescheTermin(Terminfindung terminfindung, int indexToDelete) {
		try {
			terminfindung.getVorschlaege().remove(indexToDelete);
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			return;
		}
	}
	
	public List<String> erstelleTerminfindung(Account account, Terminfindung terminfindung) {
		
		List<String> fehler = new ArrayList<String>();
		
		ArrayList<LocalDateTime> gueltigeVorschlaege = 
			LocalDateTimeManager.filterUngueltigeDaten(terminfindung.getVorschlaege());
		LocalDateTime minVorschlag = LocalDateTimeManager.bekommeFruehestesDatum(gueltigeVorschlaege);
		LocalDateTime maxVorschlag = LocalDateTimeManager.bekommeSpaetestesDatum(gueltigeVorschlaege);
		
		if (gueltigeVorschlaege.isEmpty()) {
			gueltigeVorschlaege.add(null);
			fehler.add("Es muss mindestens einen Vorschlag geben.");
		} else {
			setzeFrist(terminfindung, minVorschlag);			
			setzeLoeschdatum(terminfindung, maxVorschlag);			
		}
		
		if (LocalDateTimeManager.istVergangen(terminfindung.getFrist().minusMinutes(5))) {
			fehler.add("Die Frist ist zu kurzfristig.");
		}
		
		terminfindung.setVorschlaege(gueltigeVorschlaege);
		
		// Terminfindung erstellen
		terminfindung.setErsteller(account.getName());		
		return fehler;
	}
	
	public void setzeGruppenName(List<Terminfindung> terminfindungen, HashMap<String, String> gruppen) {
		for (Terminfindung terminfindung : terminfindungen) {
			terminfindung.setGruppeName(gruppen.get(terminfindung.getGruppeId()));
		}
	}
	
	public void updateFristUndLoeschdatum(Terminfindung terminfindung, List<LocalDateTime> neueTermine) {
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
