package mops.termine2.services;

import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.enums.Antwort;
import mops.termine2.enums.Modus;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TerminAntwortService {
	
	private TerminfindungAntwortRepository antwortRepo;
	
	public TerminAntwortService(TerminfindungAntwortRepository terminfindungAntwortRepository) {
		antwortRepo = terminfindungAntwortRepository;
	}
	
	public void abstimmen(TerminfindungAntwort antwort, Terminfindung terminVorschlag) {
		
		antwortRepo.deleteAllByTerminfindungLinkAndBenutzer(terminVorschlag.getLink(), antwort.getKuerzel());
		
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
	
	public void deleteAllByLink(String link) {
		antwortRepo.deleteAllByTerminfindungLink(link);
	}
	
	public List<TerminfindungAntwort> loadAllByLink(String link) {
		List<TerminfindungAntwortDB> terminfindungAntwortDBList =
				antwortRepo.findAllByTerminfindungLink(link);
		
		return buildAntwortenFromDB(terminfindungAntwortDBList);
	}
	
	private TerminfindungAntwort buildAntwortFromDB(List<TerminfindungAntwortDB> db) {
		if (db != null && !db.isEmpty()) {
			return buildAntwortenFromDB(db).get(0);
		}
		return null;
	}
	
	private List<TerminfindungAntwort> buildAntwortenFromDB(List<TerminfindungAntwortDB> db) {
		if (db != null && !db.isEmpty()) {
			List<String> benuternamen = new ArrayList<>();
			List<TerminfindungAntwort> terminAntworten = new ArrayList<>();
			
			for (int i = 0; i < db.size(); i++) {
				String aktuellerBenutzer = db.get(i).getBenutzer();
				if (!benuternamen.contains(db.get(i).getBenutzer())) {
					TerminfindungAntwort antwort = new TerminfindungAntwort();
					antwort.setLink(db.get(i).getTerminfindung().getLink());
					antwort.setPseudonym(db.get(i).getPseudonym());
					antwort.setKuerzel(aktuellerBenutzer);
					antwort.setGruppe(db.get(i).getTerminfindung().getGruppe());
					HashMap<LocalDateTime, Antwort> antworten = new HashMap<>();
					for (int j = 0; j < db.size(); j++) {
						if (db.get(j).getBenutzer().equals(aktuellerBenutzer)) {
							antworten.put(db.get(j).getTerminfindung().getTermin(),
									db.get(j).getAntwort());
						}
					}
					antwort.setAntworten(antworten);
					antwort.setTeilgenommen(true);
					terminAntworten.add(antwort);
					benuternamen.add(aktuellerBenutzer);
				}
				
			}
			return terminAntworten;
		}
		return null;
	}
	
}
