package mops.termine2.services;

import lombok.AllArgsConstructor;
import mops.termine2.Konstanten;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.Umfrage;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bietet Methoden im Umgang mit Links an
 */
@Service
@AllArgsConstructor
public class LinkService {
	
	private transient TerminfindungRepository terminfindungRepo;
	
	private transient UmfrageRepository umfrageRepo;
	
	/**
	 * Generiert einen eindeutigen Link
	 *
	 * @return Link in Form einer UUID
	 */
	public String generiereEindeutigenLink() {
		String link = UUID.randomUUID().toString();
		
		while (!pruefeEindeutigkeitLink(link)) {
			link = UUID.randomUUID().toString();
		}
		
		return link;
	}
	
	/**
	 * Prüft, ob ein Link eindeutig ist
	 *
	 * @param link Link, der auf Eindeutigkeit überprüft werden soll
	 * 
	 * @return {@code true}, falls der Link für keinen Termin und keine Umfrage
	 * 		   verwendet wurde, sonst {@code false}
	 */
	public Boolean pruefeEindeutigkeitLink(String link) {
		List<TerminfindungDB> terminfindungDBs = terminfindungRepo.findByLink(link);
		List<UmfrageDB> umfrageDBs = umfrageRepo.findByLink(link);
		return terminfindungDBs.isEmpty() && umfrageDBs.isEmpty();
	}
	
	/**
	 * Überprüft, ob ein Link gültiges Format hat. Das gültige Format sind alphanumerische
	 * Zeichenketten, wobei auch Bindestriche erlaubt sind.
	 * 
	 * @param link Zu überprüfender Link
	 * 
	 * @return {@code true}, falls der Link das gültige Format hat, {@code false} sonst
	 */
	public Boolean istLinkGueltig(String link) {
		return link.matches("[a-zA-Z0-9-]*");
	}
	
	/**
	 * Setzt den Link in der gegebenen Terminfindung, falls dieser nicht gesetzt ist.
	 * Ansonsten wird der Link auf Eindeutigkeit und richtige Form geprüft.
	 * Treten dabei Fehler auf, werden die entsprechenden Fehlermeldungen in eine Liste geschrieben
	 * und zurückgegeben. 
	 * 
	 * @param terminfindung Terminfindung deren Link gesetzt werden soll
	 * 
	 * @return Liste mit den aufgetretenen Fehlermeldungen. Bei Erfolg leere Liste
	 */
	public List<String> setzeOderPruefeLink(Terminfindung terminfindung) {
		List<String> fehler = new ArrayList<String>();
		if (terminfindung.getLink() == null || terminfindung.getLink().isEmpty()) {
			String link = generiereEindeutigenLink();
			terminfindung.setLink(link);
		} else {
			if (!pruefeEindeutigkeitLink(terminfindung.getLink())) {
				fehler.add(Konstanten.MESSAGE_LINK_EXISTENT);
			}
			if (!istLinkGueltig(terminfindung.getLink())) {
				fehler.add(Konstanten.MESSAGE_LINK_UNGUELTIG);
			}
		}
		return fehler;
		
	}
	
	/**
	 * Setzt den Link in der gegebenen Umfrage, falls dieser nicht gesetzt ist.
	 * Ansonsten wird der Link auf Eindeutigkeit und richtige Form geprüft.
	 * Treten dabei Fehler auf, werden die entsprechenden Fehlermeldungen in eine Liste geschrieben
	 * und zurückgegeben. 
	 * 
	 * @param umfrage Umfrage deren Link gesetzt werden soll
	 * 
	 * @return Liste mit den aufgetretenen Fehlermeldungen. Bei Erfolg leere Liste
	 */
	public List<String> setzeOderPruefeLink(Umfrage umfrage) {
		List<String> fehler = new ArrayList<String>();
		if (umfrage.getLink() == null || umfrage.getLink().isEmpty()) {
			String link = generiereEindeutigenLink();
			umfrage.setLink(link);
		} else {
			if (!pruefeEindeutigkeitLink(umfrage.getLink())) {
				fehler.add(Konstanten.MESSAGE_LINK_EXISTENT);
			}
			if (!istLinkGueltig(umfrage.getLink())) {
				fehler.add(Konstanten.MESSAGE_LINK_UNGUELTIG);
			}
		}
		return fehler;
		
	}
	
}
