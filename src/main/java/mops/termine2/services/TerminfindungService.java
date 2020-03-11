package mops.termine2.services;

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
	
	public TerminfindungService(TerminfindungRepository terminfindungRepo) {
		this.terminfindungRepo = terminfindungRepo;
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
	
	public List<Terminfindung> loadByErsteller(String ersteller) {
		List<String> links = terminfindungRepo.findLinkByErsteller(ersteller);
		return getTerminfindungenByLinks(links);
	}
	
	public List<Terminfindung> loadByGruppe(String gruppe) {
		List<String> links = terminfindungRepo.findLinkByGruppe(gruppe);
		return getTerminfindungenByLinks(links);
	}
	
	public Terminfindung loadByLink(String link) {
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
	
	public List<Terminfindung> getTerminfindungenByLinks(List<String> links) {
		if (links != null && !links.isEmpty()) {
			List<Terminfindung> terminfindungen = new ArrayList<>();
			
			for (String link : links) {
				terminfindungen.add(loadByLink(link));
			}
			return terminfindungen;
		}
		return null;
	}
}
