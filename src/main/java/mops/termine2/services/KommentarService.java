package mops.termine2.services;

import lombok.AllArgsConstructor;
import mops.termine2.database.KommentarRepository;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.KommentarDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.models.Kommentar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bietet Methoden zur Speicherung und Löschung von Kommentaren an und
 * bildet die dementsprechende Schnittstelle zwischen Controllern und Datenbank
 */
@Service
@AllArgsConstructor
public class KommentarService {
	
	private transient KommentarRepository kommentarRepo;
	
	private transient TerminfindungRepository terminfindungRepo;
	
	private transient UmfrageRepository umfrageRepo;
	
	/**
	 * Speichert einen Kommentar mit zugehörigen Link, Inhalt, Pseudonym und
	 * Erstellungsdatum in der Datenbank
	 * 
	 * @param kommentar Das zu speichernde Kommentar Objekt
	 */
	public void save(Kommentar kommentar) {
		KommentarDB kommentarDB = new KommentarDB();
		kommentarDB.setLink(kommentar.getLink());
		kommentarDB.setInhalt(kommentar.getInhalt());
		kommentarDB.setPseudonym(kommentar.getPseudonym());
		kommentarDB.setErstellungsdatum(kommentar.getErstellungsdatum());
		kommentarRepo.save(kommentarDB);
	}
	
	/**
	 * Holt alle Kommentare zum übergebenen {@code link} aus der Datenbank
	 * 
	 * @param link Der Link dessen Kommentare geholt werden sollen
	 * 
	 * @return Liste mit allen Kommentaren zu diesem Link
	 */
	public List<Kommentar> loadByLink(String link) {
		List<KommentarDB> kommentarDBs = kommentarRepo.findByLinkOrderByErstellungsdatumAsc(link);
		List<Kommentar> kommentare = new ArrayList<>();
		for (KommentarDB kommentarDB : kommentarDBs) {
			kommentare.add(erstelleKommentar(kommentarDB));
		}
		return kommentare;
	}
	
	/**
	 * Löscht alle Kommentare, die zu einer Terminfindung gehören, deren
	 * Löschdatum überschritten wurde
	 */
	@Transactional
	public void loescheAbgelaufeneKommentareFuerTermine() {
		LocalDateTime timeNow = LocalDateTime.now();
		List<TerminfindungDB> terminfindungen = terminfindungRepo.findByLoeschdatumBefore(timeNow);
		List<String> links = new ArrayList<>();
		for (TerminfindungDB terminfindung : terminfindungen) {
			if (!links.contains(terminfindung.getLink())) {
				links.add(terminfindung.getLink());
			}
		}
		for (String link : links) {
			kommentarRepo.deleteByLink(link);
		}
	}
	
	/**
	 * Löscht alle Kommentare, die zu einer Umfrage gehören, deren
	 * Löschdatum überschritten wurde
	 */
	@Transactional
	public void loescheAbgelaufeneKommentareFuerUmfragen() {
		LocalDateTime timeNow = LocalDateTime.now();
		List<UmfrageDB> umfragen = umfrageRepo.findByLoeschdatumBefore(timeNow);
		List<String> links = new ArrayList<>();
		for (UmfrageDB umfrage : umfragen) {
			if (!links.contains(umfrage.getLink())) {
				links.add(umfrage.getLink());
			}
		}
		for (String link : links) {
			kommentarRepo.deleteByLink(link);
		}
	}
	
	private Kommentar erstelleKommentar(KommentarDB kommentarDB) {
		Kommentar kommentar = new Kommentar();
		kommentar.setLink(kommentarDB.getLink());
		kommentar.setInhalt(kommentarDB.getInhalt());
		kommentar.setPseudonym(kommentarDB.getPseudonym());
		kommentar.setErstellungsdatum(kommentarDB.getErstellungsdatum());
		return kommentar;
	}
}
