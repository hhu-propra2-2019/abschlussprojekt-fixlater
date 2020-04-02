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
import java.util.Objects;

@Service

public class TerminfindungAntwortService {
	
	private TerminfindungAntwortRepository antwortRepo;
	
	private TerminfindungRepository terminRepo;
	
	public TerminfindungAntwortService(TerminfindungAntwortRepository terminfindungAntwortRepository,
								TerminfindungRepository terminfindungRepository) {
		antwortRepo = terminfindungAntwortRepository;
		terminRepo = terminfindungRepository;
	}
	
	/**
	 * Speichert Antworten zu einer Terminabstimmung
	 *
	 * @param antwort
	 * @param terminVorschlag
	 */
	public void abstimmen(TerminfindungAntwort antwort, Terminfindung terminVorschlag) {
		
		List<TerminfindungAntwortDB> antwortenToDelete =
			antwortRepo.findByBenutzerAndTerminfindungLink(antwort.getKuerzel(),
				terminVorschlag.getLink());
		
		antwortRepo.deleteAll(antwortenToDelete);
		for (LocalDateTime termin : terminVorschlag.getVorschlaege()) {
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
	
	public boolean hatNutzerAbgestimmt(String benutzer, String link) {
		List<TerminfindungAntwortDB> antworten =
			antwortRepo.findByBenutzerAndTerminfindungLink(benutzer,
				link);
		return !antworten.isEmpty();
	}
	
	/**
	 * Löscht alle Antworten nach Link
	 *
	 * @param link
	 */
	public void deleteAllByLink(String link) {
		antwortRepo.deleteByTerminfindungLink(link);
	}
	
	/**
	 * Lädt eine Liste von Antworten nach Benutzer und Link
	 *
	 * @param benutzer der Benutzer dessen Antwort gesucht wir
	 * @param link     der Link der Terminumfrage
	 * @return gibt eine Antwort zu einer Terminfindung
	 */
	
	public TerminfindungAntwort loadByBenutzerAndLink(String benutzer, String link) {
		
		List<TerminfindungAntwortDB> alteAntwort =
			antwortRepo.findByBenutzerAndTerminfindungLink(benutzer, link);
		List<TerminfindungDB> antwortMoeglichkeiten = terminRepo.findByLink(link);
		
		return buildAntwortForBenutzer(benutzer, alteAntwort,
			antwortMoeglichkeiten);
	}
	
	/**
	 * Lädt alle Antworten die zu einem Link gehören
	 *
	 * @param link der Link der Terminumfrage
	 * @return eine Liste von Antworten
	 */
	public List<TerminfindungAntwort> loadAllByLink(String link) {
		List<TerminfindungAntwortDB> terminfindungAntwortDBList =
			antwortRepo.findAllByTerminfindungLink(link);
		List<TerminfindungDB> antwortMoeglichkeiten = terminRepo.findByLink(link);
		
		return buildAntworten(terminfindungAntwortDBList,
			antwortMoeglichkeiten);
	}
	
	private List<TerminfindungAntwort> buildAntworten(
		List<TerminfindungAntwortDB> antwortDBS, List<TerminfindungDB> antwortMoeglichkeiten) {
		
		List<TerminfindungAntwort> terminAntworten = new ArrayList<>();
		if (!antwortDBS.isEmpty()) {
			List<String> benuternamen = new ArrayList<>();
			
			for (TerminfindungAntwortDB antwortDB : antwortDBS) {
				String benutzer = antwortDB.getBenutzer();
				if (!benuternamen.contains(antwortDB.getBenutzer())) {
					List<TerminfindungAntwortDB> nutzerAntworten = filterAntwortenDbBenutzer(
						antwortDBS, benutzer);
					terminAntworten.add(buildAntwortForBenutzer(
						benutzer, nutzerAntworten, antwortMoeglichkeiten));
					benuternamen.add(benutzer);
				}
				
			}
		}
		return terminAntworten;
		
	}
	
	private TerminfindungAntwort buildAntwortForBenutzer(
		String benutzer, List<TerminfindungAntwortDB> alteAntworten,
		List<TerminfindungDB> antwortMoglichkeiten) {
		
		TerminfindungAntwort antwort = new TerminfindungAntwort();
		antwort.setKuerzel(benutzer);
		antwort.setLink(antwortMoglichkeiten.get(0).getLink());
		if (!alteAntworten.isEmpty()) {
			antwort.setPseudonym(alteAntworten.get(0).getPseudonym());
		} else {
			antwort.setPseudonym(benutzer);
		}
		
		HashMap<LocalDateTime, Antwort> alteAntwortenMap = new HashMap<>();
		for (TerminfindungAntwortDB alteAntwort : alteAntworten) {
			alteAntwortenMap.put(alteAntwort.getTerminfindung().getTermin(), alteAntwort.getAntwort());
		}
		HashMap<LocalDateTime, Antwort> antwortenMap = new HashMap<>();
		
		for (TerminfindungDB antwortMoglichkeit : antwortMoglichkeiten) {
			LocalDateTime termin = antwortMoglichkeit.getTermin();
			Antwort alteAntwort = alteAntwortenMap.get(termin);
			antwortenMap.put(termin, Objects.requireNonNullElse(alteAntwort, Antwort.VIELLEICHT));
		}
		
		antwort.setAntworten(antwortenMap);
		return antwort;
	}
	
	
	private List<TerminfindungAntwortDB> filterAntwortenDbBenutzer(
		List<TerminfindungAntwortDB> antwortDBS, String benutzer) {
		List<TerminfindungAntwortDB> nutzerAntwortenDB = new ArrayList<>();
		for (TerminfindungAntwortDB terminAntwortDB : antwortDBS) {
			if (benutzer.equals(terminAntwortDB.getBenutzer())) {
				nutzerAntwortenDB.add(terminAntwortDB);
			}
		}
		return nutzerAntwortenDB;
	}
}
