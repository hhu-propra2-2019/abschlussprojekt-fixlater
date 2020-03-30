package mops.termine2.services;

import lombok.AllArgsConstructor;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.models.Terminfindung;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LinkService {
	
	private transient TerminfindungRepository terminfindungRepo;
	
	private transient UmfrageRepository umfrageRepo;
	
	/**
	 * Generiert einen eindeutigen Link
	 *
	 * @return eindeutiger Link
	 */
	public String generiereEindeutigenLink() {
		String link = UUID.randomUUID().toString();
		
		while (!pruefeEindeutigkeitLink(link)) {
			link = UUID.randomUUID().toString();
		}
		
		return link;
	}
	
	/**
	 * Prüft ob ein Link eindeutig ist
	 *
	 * @param link vom Link Generator übergeben
	 * @return boolean zur Bestätigung der Eindeutigkeit
	 */
	public Boolean pruefeEindeutigkeitLink(String link) {
		List<TerminfindungDB> terminfindungDBs = terminfindungRepo.findByLink(link);
		List<UmfrageDB> umfrageDBs = umfrageRepo.findByLink(link);
		
		return terminfindungDBs.isEmpty() && umfrageDBs.isEmpty();
	}
	
	public Boolean isLinkValid(String link) {
		return link.matches("[a-zA-Z0-9-]*");
	}

	public List<String> setzeLink(Terminfindung terminfindung) {
		List<String> fehler = new ArrayList<String>();
		if (terminfindung.getLink().isEmpty()) {
			String link = generiereEindeutigenLink();
			terminfindung.setLink(link);
		} else {
			if (!pruefeEindeutigkeitLink(terminfindung.getLink())) {
				fehler.add("Der eingegebene Link existiert bereits.");
			}
			if (!isLinkValid(terminfindung.getLink())) {
				fehler.add("Der eingegebene Link enthält ungültige Zeichen");
			}
		}
		return fehler;
		
	}
	
}
