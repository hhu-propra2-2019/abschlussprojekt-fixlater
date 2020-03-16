package mops.termine2.services;

import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.enums.Modus;
import mops.termine2.models.Terminfindung;
import org.springframework.stereotype.Service;

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
	
	public void save(Terminfindung terminfindung) {
		
		for (LocalDateTime termin : terminfindung.getVorschlaege()) {
			TerminfindungDB terminfindungDB = new TerminfindungDB();
			terminfindungDB.setTitel(terminfindung.getTitel());
			terminfindungDB.setOrt(terminfindung.getOrt());
			terminfindungDB.setErsteller(terminfindung.getErsteller());
			terminfindungDB.setFrist(terminfindung.getFrist());
			terminfindungDB.setLoeschdatum(terminfindung.getLoeschdatum());
			terminfindungDB.setLink(terminfindung.getLink());
			terminfindungDB.setBeschreibung(terminfindung.getBeschreibung());
			terminfindungDB.setGruppe(terminfindung.getGruppe());
			terminfindungDB.setTermin(termin);
			
			if (terminfindung.getGruppe() != null) {
				terminfindungDB.setModus(Modus.GRUPPE);
			} else {
				terminfindungDB.setModus(Modus.LINK);
			}
			
			terminfindungRepo.save(terminfindungDB);
		}
	}
	
	public void loescheByLink(String link) {
		antwortRepo.deleteByLink(link);
		terminfindungRepo.deleteByLink(link);
	}
	
	public void loescheAbgelaufene() {
		LocalDateTime timeNow = LocalDateTime.now();
		antwortRepo.loescheAelterAls(timeNow);
		terminfindungRepo.loescheAelterAls(timeNow);
	}
	
	
	public List<Terminfindung> loadByErstellerOhneTermine(String ersteller) {
		List<TerminfindungDB> terminfindungDBs = terminfindungRepo.findByErsteller(ersteller);
		List<Terminfindung> terminfindungen = getDistinctTerminfindungList(terminfindungDBs);
		return terminfindungen;
	}
	
	public List<Terminfindung> loadByGruppeOhneTermine(String gruppe) {
		List<TerminfindungDB> terminfindungDBs = terminfindungRepo.findByGruppe(gruppe);
		List<Terminfindung> terminfindungen = getDistinctTerminfindungList(terminfindungDBs);
		return terminfindungen;
	}
	
	public List<Terminfindung> loadAllBenutzerHatAbgestimmtOhneTermine(String benutzer) {
		List<TerminfindungDB> terminfindungDBs = antwortRepo.findTerminfindungDbByBenutzer(benutzer);
		List<Terminfindung> terminfindungen = getDistinctTerminfindungList(terminfindungDBs);
		return terminfindungen;
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
			terminfindung.setGruppe(ersterTermin.getGruppe());
			terminfindung.setLink(ersterTermin.getLink());
			terminfindung.setErsteller(ersterTermin.getErsteller());
			
			List<LocalDateTime> terminMoeglichkeiten = new ArrayList<>();
			for (TerminfindungDB termin : termineDB) {
				terminMoeglichkeiten.add(termin.getTermin());
			}
			terminfindung.setVorschlaege(terminMoeglichkeiten);
			return terminfindung;
		}
		return null;
	}
	
	private List<Terminfindung> getDistinctTerminfindungList(List<TerminfindungDB> terminfindungDBs) {
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
	
	private Terminfindung erstelleTerminfindungOhneTermine(TerminfindungDB db) {
		Terminfindung terminfindung = new Terminfindung();
		terminfindung.setLink(db.getLink());
		terminfindung.setTitel(db.getTitel());
		terminfindung.setErsteller(db.getErsteller());
		terminfindung.setLoeschdatum(db.getLoeschdatum());
		terminfindung.setFrist(db.getFrist());
		terminfindung.setGruppe(db.getGruppe());
		terminfindung.setBeschreibung(db.getBeschreibung());
		terminfindung.setOrt(db.getOrt());
		
		return terminfindung;
	}
	
	
}
