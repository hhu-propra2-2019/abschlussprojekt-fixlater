package mops.termine2.services;

import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.enums.Modus;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;

import java.time.LocalDateTime;

public class TerminAntwortService {
	
	private TerminfindungAntwortRepository antwortRepo;
	
	private TerminfindungService terminfindungService;
	
	public TerminAntwortService(TerminfindungAntwortRepository terminfindungAntwortRepository,
								TerminfindungService terminFService) {
		this.terminfindungService = terminFService;
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
}
