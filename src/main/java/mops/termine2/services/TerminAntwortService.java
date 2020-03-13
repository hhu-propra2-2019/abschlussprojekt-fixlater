package mops.termine2.services;

import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.enums.Antwort;
import mops.termine2.enums.Modus;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class TerminAntwortService {
	
	private TerminfindungAntwortRepository antwortRepo;
	
	public TerminAntwortService(TerminfindungAntwortRepository terminfindungAntwortRepository) {
		antwortRepo = terminfindungAntwortRepository;
	}
	
	public void abstimmen(TerminfindungAntwort antwort, Terminfindung terminVorschlag) {
		
		antwortRepo.deleteAllByTerminfindungLinkAAndBenutzer(terminVorschlag.getLink(), antwort.getKuerzel());
		
		for (LocalDateTime termin : antwort.getAntworten().keySet()) {
			TerminfindungAntwortDB db = new TerminfindungAntwortDB();
			TerminfindungDB terminfindungDB = new TerminfindungDB();
			terminfindungDB.setTitel(terminVorschlag.getTitel());
			terminfindungDB.setErsteller(terminVorschlag.getErsteller());
			terminfindungDB.setBeschreibung(terminVorschlag.getBeschreibung());
			terminfindungDB.setOrt(terminVorschlag.getOrt());
			terminfindungDB.setLink(terminVorschlag.getLink());
			terminfindungDB.setFrist(terminVorschlag.getFrist());
			terminfindungDB.setLoeschdatum(terminVorschlag.getLoeschdatum());
			if (terminVorschlag.getGruppe() == null) {
				terminfindungDB.setModus(Modus.LINK);
			} else {
				terminfindungDB.setModus(Modus.GRUPPE);
			}
			terminfindungDB.setTermin(termin);
			
			db.setAntwort(antwort.getAntworten().get(termin));
			db.setBenutzer(antwort.getKuerzel());
			db.setPseudonym(antwort.getPseudonym());
			db.setTerminfindung(terminfindungDB);
			antwortRepo.save(db);
		}
	}
	
	public TerminfindungAntwort loadByBenutzerAndLink(String benutzer, String link) {
		List<TerminfindungAntwortDB> terminfindungAntwortDBList =
				antwortRepo.findByBenutzerAndTerminfindungLink(benutzer, link);
		return buildAntwortFromDB(terminfindungAntwortDBList);
	}
	
	private TerminfindungAntwort buildAntwortFromDB(List<TerminfindungAntwortDB> db) {
		if (db != null && !db.isEmpty()) {
			TerminfindungAntwort antwort = new TerminfindungAntwort();
			antwort.setGruppe(db.get(0).getTerminfindung().getGruppe());
			antwort.setKuerzel(db.get(0).getBenutzer());
			antwort.setLink(db.get(0).getTerminfindung().getLink());
			antwort.setTeilgenommen(true);
			antwort.setPseudonym(db.get(0).getPseudonym());
			
			HashMap<LocalDateTime, Antwort> antworten = new HashMap<>();
			for (TerminfindungAntwortDB anDB : db) {
				antworten.put(anDB.getTerminfindung().getTermin(), anDB.getAntwort());
			}
			
			antwort.setAntworten(antworten);
			return antwort;
		}
		return null;
	}
	
	
}
