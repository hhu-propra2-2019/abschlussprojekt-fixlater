package mops.termine2.services;

import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service

public class TerminAntwortService {
	
	private TerminfindungAntwortRepository antwortRepo;
	
	private TerminfindungRepository terminRepo;
	
	public TerminAntwortService(TerminfindungAntwortRepository terminfindungAntwortRepository,
								TerminfindungRepository terminfindungRepository) {
		antwortRepo = terminfindungAntwortRepository;
		terminRepo = terminfindungRepository;
	}
	
	public void abstimmen(TerminfindungAntwort antwort, Terminfindung terminVorschlag) {
		
		//antwortRepo.deleteAllByTerminfindungLinkAndBenutzer(terminVorschlag.getLink(), antwort.getKuerzel());
		List<TerminfindungAntwortDB> antwortenToDelete =
				antwortRepo.findByBenutzerAndTerminfindungLink(antwort.getKuerzel(),
						terminVorschlag.getLink());
		
		antwortRepo.deleteAll(antwortenToDelete);
		for (LocalDateTime termin : antwort.getAntworten().keySet()) {
			TerminfindungAntwortDB db = new TerminfindungAntwortDB();
			TerminfindungDB terminfindungDB = terminRepo.findByLinkAndTermin(terminVorschlag.getLink(),
					termin);
			
			db.setAntwort(antwort.getAntworten().get(termin));
			db.setBenutzer(antwort.getKuerzel());
			db.setPseudonym(antwort.getPseudonym());
			db.setTerminfindung(terminfindungDB);
			if (terminfindungDB != null) {
				antwortRepo.save(db);
			}
		}
	}
	
	public void deleteAllByLink(String link) {
		antwortRepo.deleteByLink(link);
	}
	
	public TerminfindungAntwort loadByBenutzerAndLink(String benutzer, String link) {
		List<TerminfindungAntwortDB> terminfindungAntwortDBList =
				antwortRepo.findByBenutzerAndTerminfindungLink(benutzer, link);
		
		TerminfindungAntwort antwort = buildAntwortFromDB(terminfindungAntwortDBList);
		if (antwort == null) {
			antwort = erstelleLeereAntwort(benutzer, link);
		}
		
		return aktuelleOptionenEinfuegen(antwort, link);
	}
	
	private TerminfindungAntwort erstelleLeereAntwort(String benutzer, String link) {
		TerminfindungAntwort antwort = new TerminfindungAntwort();
		antwort.setPseudonym(benutzer);
		antwort.setKuerzel(benutzer);
		antwort.setLink(link);
		antwort.setAntworten(new HashMap<>());
		return antwort;
	}
	
	public List<TerminfindungAntwort> loadAllByLink(String link) {
		List<TerminfindungAntwortDB> terminfindungAntwortDBList =
				antwortRepo.findAllByTerminfindungLink(link);
		
		List<TerminfindungAntwort> antworten = buildAntwortenFromDB(terminfindungAntwortDBList);
		if (antworten == null) {
			antworten = new ArrayList<>();
		}
		for (TerminfindungAntwort antwort : antworten) {
			aktuelleOptionenEinfuegen(antwort, link);
		}
		return antworten;
	}
	
	public boolean hatNutzerAbgestimmt(String benutzer, String link) {
		List<TerminfindungAntwortDB> antworten =
				antwortRepo.findByBenutzerAndTerminfindungLink(benutzer,
						link);
		if (antworten.isEmpty()) {
			return false;
		}
		return true;
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
			
			for (TerminfindungAntwortDB antwortDB : db) {
				String aktuellerBenutzer = antwortDB.getBenutzer();
				if (!benuternamen.contains(antwortDB.getBenutzer())) {
					TerminfindungAntwort antwort = new TerminfindungAntwort();
					antwort.setLink(antwortDB.getTerminfindung().getLink());
					antwort.setPseudonym(antwortDB.getPseudonym());
					antwort.setKuerzel(aktuellerBenutzer);
					antwort.setGruppe(antwortDB.getTerminfindung().getGruppe());
					HashMap<LocalDateTime, Antwort> antworten = new HashMap<>();
					for (TerminfindungAntwortDB terminfindungAntwortDB : db) {
						if (terminfindungAntwortDB.getBenutzer().equals(aktuellerBenutzer)) {
							antworten.put(terminfindungAntwortDB
											.getTerminfindung().getTermin(),
									terminfindungAntwortDB.getAntwort());
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
	
	private TerminfindungAntwort aktuelleOptionenEinfuegen(TerminfindungAntwort antwort, String link) {
		List<TerminfindungDB> terminfindungDBS = terminRepo.findByLink(link);
		HashMap<LocalDateTime, Antwort> antworten = new HashMap<>();
		HashMap<LocalDateTime, Antwort> alteAntworten = antwort.getAntworten();
		for (TerminfindungDB db : terminfindungDBS) {
			LocalDateTime termin = db.getTermin();
			if (alteAntworten.get(termin) != null) {
				antworten.put(termin, alteAntworten.get(termin));
			} else {
				antworten.put(termin, Antwort.VIELLEICHT);
			}
		}
		antwort.setAntworten(antworten);
		return antwort;
	}
	
}
