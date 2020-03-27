package mops.termine2.services;

import mops.termine2.controller.formular.ErgebnisForm;
import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;
import mops.termine2.util.IntegerToolkit;
import mops.termine2.util.LocalDateTimeManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TerminErgebnisService {
	
	private TerminfindungAntwortRepository antwortRepo;
	
	public TerminErgebnisService(TerminfindungAntwortRepository antwortRepo) {
		this.antwortRepo = antwortRepo;
	}
	
	
	public LocalDateTime berechneErgebnisTerminfindung(Terminfindung terminfindung) {
		List<TerminfindungAntwortDB> terminfindungAntwortDBS =
			antwortRepo.findAllByTerminfindungLink(terminfindung.getLink());
		
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
		
		List<Integer> highest = IntegerToolkit.findHighestIndex(ja);
		if (highest.size() > 1) {
			highest = IntegerToolkit.findHighestIndex(vielleicht);
		}
		
		return terminfindung.getVorschlaege().get(highest.get(0));
	}
	
	
	public ErgebnisForm baueErgebnisForm(
		List<TerminfindungAntwort> antworten,
		Terminfindung terminfindung,
		TerminfindungAntwort nutzerAbstimmung) {
		
		ErgebnisForm toReturn = new ErgebnisForm();
		int anzahlAntworten;
		
		List<LocalDateTime> termine = new ArrayList<>();
		
		List<Antwort> nutzerAntworten = new ArrayList<>();
		
		List<String> termineString = new ArrayList<>();
		
		List<Integer> anzahlStimmenJa = new ArrayList<>();
		
		List<Integer> anzahlStimmenVielleicht = new ArrayList<>();
		
		List<Integer> anzahlStimmenNein = new ArrayList<>();
		
		List<Double> anteilStimmenJa = new ArrayList<>();
		
		List<Double> anteilStimmenVielleicht = new ArrayList<>();
		
		List<Double> anteilStimmenNein = new ArrayList<>();
		
		List<Boolean> isNutzerAntwortJa = new ArrayList<>();
		
		List<Boolean> isNutzerAntwortVielleicht = new ArrayList<>();
		
		List<String> jaAntwortPseud = new ArrayList<>();
		
		List<String> vielleichtAntwortPseudo = new ArrayList<>();
		
		List<String> neinAntwortPseudo = new ArrayList<>();
		
		boolean fristNichtAbgelaufen = false;
		
		String ergebnis = "eine Zeit";
		
		HashMap<LocalDateTime, Antwort> nutzerAntwortenMap = nutzerAbstimmung.getAntworten();
		termine = terminfindung.getVorschlaege();
		LocalDateTimeManager.sortTermine(termine);
		anzahlAntworten = antworten.size();
		
		LocalDateTime now = LocalDateTime.now();
		if (terminfindung.getFrist().isBefore(now)) {
			fristNichtAbgelaufen = false;
		} else {
			fristNichtAbgelaufen = true;
		}
		
		for (LocalDateTime localDateTime : termine) {
			int ja = 0;
			int nein = 0;
			int vielleicht = 0;
			String jaAnt = "";
			String vielleichtAnt = "";
			String neinAnt = "";
			
			nutzerAntworten.add(nutzerAntwortenMap.get(localDateTime));
			for (TerminfindungAntwort antwort : antworten) {
				HashMap<LocalDateTime, Antwort> antwortMap = antwort.getAntworten();
				Antwort a = antwortMap.get(localDateTime);
				String pseudonym = antwort.getPseudonym();
				if (a == Antwort.JA) {
					ja++;
					jaAnt = jaAnt + pseudonym + " ; ";
				} else if (a == Antwort.NEIN) {
					nein++;
					neinAnt = neinAnt + pseudonym + " ; ";
				} else {
					vielleicht++;
					vielleichtAnt = vielleichtAnt + pseudonym + " ; ";
				}
				
			}
			termineString.add(LocalDateTimeManager.toString(localDateTime));
			anzahlStimmenJa.add(ja);
			anzahlStimmenNein.add(nein);
			anzahlStimmenVielleicht.add(vielleicht);
			jaAntwortPseud.add(jaAnt);
			vielleichtAntwortPseudo.add(vielleichtAnt);
			neinAntwortPseudo.add(neinAnt);
			
			double jaAnteil = 100 * (ja * 1.) / anzahlAntworten;
			double vielleichtAnteil = 100 * (vielleicht * 1.) / anzahlAntworten;
			double neinAnteil = 100 * (nein * 1.) / anzahlAntworten;
			anteilStimmenJa.add(jaAnteil);
			anteilStimmenVielleicht.add(vielleichtAnteil);
			anteilStimmenNein.add(neinAnteil);
			
			isNutzerAntwortJa.add(nutzerAntwortenMap.get(localDateTime).equals(Antwort.JA));
			isNutzerAntwortVielleicht.add(nutzerAntwortenMap.get(localDateTime).equals(Antwort.VIELLEICHT));
			
			
		}
		
		toReturn.setAnzahlAntworten(anzahlAntworten);
		toReturn.setNutzerAntworten(nutzerAntworten);
		toReturn.setTermineString(termineString);
		toReturn.setAnzahlStimmenJa(anzahlStimmenJa);
		toReturn.setAnzahlStimmenNein(anzahlStimmenNein);
		toReturn.setAnzahlStimmenVielleicht(anzahlStimmenVielleicht);
		toReturn.setAnteilStimmenJa(anteilStimmenJa);
		toReturn.setAnteilStimmenVielleicht(anteilStimmenVielleicht);
		toReturn.setAnteilStimmenNein(anteilStimmenNein);
		toReturn.setIsNutzerAntwortJa(isNutzerAntwortJa);
		toReturn.setIsNutzerAntwortVielleicht(isNutzerAntwortVielleicht);
		toReturn.setJaAntwortPseud(jaAntwortPseud);
		toReturn.setVielleichtAntwortPseudo(vielleichtAntwortPseudo);
		toReturn.setNeinAntwortPseudo(neinAntwortPseudo);
		toReturn.setFristNichtAbgelaufen(fristNichtAbgelaufen);
		toReturn.setErgebnis(LocalDateTimeManager.toString(berechneErgebnisTerminfindung(terminfindung)));
		
		return toReturn;
	}
}
