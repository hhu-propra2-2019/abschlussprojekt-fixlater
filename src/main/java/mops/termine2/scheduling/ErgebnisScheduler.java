package mops.termine2.scheduling;

import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.Umfrage;
import mops.termine2.services.TerminfindungErgebnisService;
import mops.termine2.services.TerminfindungService;
import mops.termine2.services.UmfrageErgebnisService;
import mops.termine2.services.UmfrageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Component
@EnableScheduling
public class ErgebnisScheduler {
	
	private final Logger logger = Logger.getLogger(ErgebnisScheduler.class.getName());
	
	@Autowired
	private TerminfindungRepository terminfindungRepository;
	
	@Autowired
	private TerminfindungService terminfindungService;
	
	@Autowired
	private UmfrageRepository umfrageRepository;
	
	@Autowired
	private UmfrageService umfrageService;
	
	@Autowired
	private TerminfindungErgebnisService terminErgebnisService;
	
	@Autowired
	private UmfrageErgebnisService umfrageErgebnisService;
	
	@Scheduled(cron = "0 * * * * *")
	public void ergebnis() {
		int anzahlTermine = ergebnisTerminfindung();
		int anzahlUmfragen = ergebnisUmfrage();
		
		logger.info("Es wurden " + (anzahlTermine + anzahlUmfragen) + " Ergebnisse berechnet");
	}
	
	private int ergebnisTerminfindung() {
		List<TerminfindungDB> terminfindungDBS =
			terminfindungRepository
				.findByFristBeforeAndErgebnisIsNull(LocalDateTime.now());
		
		List<Terminfindung> terminfindungen =
			terminfindungService.getEindeutigeTerminfindungen(terminfindungDBS);
		
		for (Terminfindung termin : terminfindungen) {
			Terminfindung aktuellerTermin = terminfindungService.loadByLinkMitTerminen(termin.getLink());
			LocalDateTime ergebnis = terminErgebnisService.berechneErgebnisTerminfindung(aktuellerTermin);
			aktuellerTermin.setErgebnis(ergebnis);
			
			terminfindungService.save(aktuellerTermin);
		}
		return terminfindungen.size();
	}
	
	private int ergebnisUmfrage() {
		List<UmfrageDB> umfrageDBS =
			umfrageRepository
				.findByFristBeforeAndErgebnisIsNull(LocalDateTime.now());
		
		List<Umfrage> umfragen =
			umfrageService.getEindeutigeUmfragen(umfrageDBS);
		
		for (Umfrage umfrage : umfragen) {
			Umfrage aktuelleUmfrage = umfrageService.loadByLinkMitVorschlaegen(umfrage.getLink());
			String ergebnis = umfrageErgebnisService.berechneErgebnisUmfrage(aktuelleUmfrage);
			aktuelleUmfrage.setErgebnis(ergebnis);
			
			umfrageService.save(aktuelleUmfrage);
		}
		return umfragen.size();
	}
	
}
