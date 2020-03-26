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

@Service
@AllArgsConstructor
public class KommentarService {
	
	private transient KommentarRepository kommentarRepo;
	
	private transient TerminfindungRepository terminfindungRepo;
	
	private transient UmfrageRepository umfrageRepo;
	
	/**
	 * Speichert einen Kommentar mit zugehörigen Link, Inhalt, Pseudonym und
	 * ErstellungsDatum in einer KommentarDB
	 * 
	 * @param kommentar bekommt einen Kommentar übergeben
	 */
	public void save(Kommentar kommentar) {
		KommentarDB kommentarDB = new KommentarDB();
		kommentarDB.setLink(kommentar.getLink());
		kommentarDB.setInhalt(kommentar.getInhalt());
		kommentarDB.setPseudonym(kommentar.getPseudonym());
		kommentarDB.setErstellungsdatum(kommentar.getErstellungsdatum());
		kommentarRepo.save(kommentarDB);
	}
	
	public List<Kommentar> loadByLink(String link) {
		List<KommentarDB> kommentarDBs = kommentarRepo.findByLinkOrderByErstellungsdatumAsc(link);
		List<Kommentar> kommentare = new ArrayList<>();
		for (KommentarDB kommentarDB : kommentarDBs) {
			kommentare.add(erstelleKommentar(kommentarDB));
		}
		return kommentare;
	}
	
	private Kommentar erstelleKommentar(KommentarDB kommentarDB) {
		Kommentar kommentar = new Kommentar();
		kommentar.setLink(kommentarDB.getLink());
		kommentar.setInhalt(kommentarDB.getInhalt());
		kommentar.setPseudonym(kommentarDB.getPseudonym());
		kommentar.setErstellungsdatum(kommentarDB.getErstellungsdatum());
		return kommentar;
	}
	
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
}
