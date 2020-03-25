package mops.termine2.services;

import lombok.AllArgsConstructor;
import mops.termine2.database.KommentarRepository;
import mops.termine2.database.entities.KommentarDB;
import mops.termine2.models.Kommentar;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class KommentarService {
	
	private transient KommentarRepository kommentarRepo;
	
	/**
	 * Speichert einen Kommentar mit zugehörigen
	 * Link, Inhalt, Pseudonym und ErstellungsDatum
	 * in einer KommentarDB
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
}
