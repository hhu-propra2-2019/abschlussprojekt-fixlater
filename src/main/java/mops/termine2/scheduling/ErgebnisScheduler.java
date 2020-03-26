package mops.termine2.scheduling;

import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.Umfrage;
import mops.termine2.services.TerminfindungService;
import mops.termine2.services.UmfrageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
	private TerminfindungAntwortRepository terminfindungAntwortRepository;
	
	@Autowired
	private UmfrageRepository umfrageRepository;
	
	@Autowired
	private UmfrageService umfrageService;
	
	@Autowired
	private UmfrageAntwortRepository umfrageAntwortRepository;
	
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
			terminfindungService.getDistinctTerminfindungList(terminfindungDBS);
		
		for (Terminfindung termin : terminfindungen) {
			Terminfindung aktuellerTermin = terminfindungService.loadByLinkMitTerminen(termin.getLink());
			LocalDateTime ergebnis = berechneErgebnisTerminfindung(aktuellerTermin);
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
			umfrageService.getDistinctUmfragen(umfrageDBS);
		
		for (Umfrage umfrage : umfragen) {
			Umfrage aktuelleUmfrage = umfrageService.loadByLinkMitVorschlaegen(umfrage.getLink());
			String ergebnis = berechneErgebnisUmfrage(aktuelleUmfrage);
			aktuelleUmfrage.setErgebnis(ergebnis);
			
			umfrageService.save(aktuelleUmfrage);
		}
		return umfragen.size();
	}
	
	private List<Integer> findHighestIndex(int[] votes) {
		List<Integer> highest = new ArrayList<>();
		int highestValue = 0;
		
		for (int i = 0; i < votes.length; i++) {
			if (votes[i] >= highestValue) {
				if (votes[i] > highestValue) {
					highest.clear();
				}
				highest.add(i);
				highestValue = votes[i];
			}
		}
		
		return highest;
	}
	
	private LocalDateTime berechneErgebnisTerminfindung(Terminfindung terminfindung) {
		List<TerminfindungAntwortDB> terminfindungAntwortDBS =
			terminfindungAntwortRepository.findAllByTerminfindungLink(terminfindung.getLink());
		
		List<LocalDateTime> termine = terminfindung.getVorschlaege();
		
		int[] ja = new int[termine.size()];
		int[] nein = new int[termine.size()];
		int[] vielleicht = new int[termine.size()];
		
		for (TerminfindungAntwortDB terminfindungAntwortDB : terminfindungAntwortDBS) {
			int index = termine.indexOf(terminfindungAntwortDB.getTerminfindung().getTermin());
			switch (terminfindungAntwortDB.getAntwort()) {
			case JA:
				ja[index]++;
				break;
			case NEIN:
				nein[index]++;
				break;
			case VIELLEICHT:
				vielleicht[index]++;
				break;
			default:
				break;
			}
		}
		
		List<Integer> highest = findHighestIndex(ja);
		if (highest.size() > 1) {
			highest = findHighestIndex(vielleicht);
		}
		
		return terminfindung.getVorschlaege().get(highest.get(0));
	}
	
	private String berechneErgebnisUmfrage(Umfrage umfrage) {
		List<UmfrageAntwortDB> umfrageAntwortDBS =
			umfrageAntwortRepository.findAllByUmfrageLink(umfrage.getLink());
		
		List<String> vorschlaege = umfrage.getVorschlaege();
		
		int[] ja = new int[vorschlaege.size()];
		int[] nein = new int[vorschlaege.size()];
		int[] vielleicht = new int[vorschlaege.size()];
		
		for (UmfrageAntwortDB umfrageAntwortDB : umfrageAntwortDBS) {
			int index = vorschlaege.indexOf(umfrageAntwortDB.getUmfrage().getAuswahlmoeglichkeit());
			switch (umfrageAntwortDB.getAntwort()) {
			case JA:
				ja[index]++;
				break;
			case NEIN:
				nein[index]++;
				break;
			default:
				break;
			}
		}
		
		List<Integer> highest = findHighestIndex(ja);
		
		return umfrage.getVorschlaege().get(highest.get(0));
	}
}
