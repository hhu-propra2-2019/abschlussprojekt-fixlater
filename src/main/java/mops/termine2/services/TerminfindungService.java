package mops.termine2.services;

import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.models.Terminfindung;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TerminfindungService {
	
	private final TerminfindungRepository terminfindungRepo;
	
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
			
			terminfindungRepo.save(terminfindungDB);
		}
	}
	
	public Terminfindung loadByLink(String link) {
		return null;
	}
	
	public List<Terminfindung> loadByBenutzer(String benutzer) {
		return null;
	}
	
}
