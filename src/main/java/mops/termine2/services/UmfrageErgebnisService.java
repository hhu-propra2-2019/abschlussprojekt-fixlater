package mops.termine2.services;

import mops.termine2.controller.formular.ErgebnisFormUmfragen;
import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Umfrage;
import mops.termine2.models.UmfrageAntwort;
import mops.termine2.util.IntegerToolkit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Bietet Methoden zur Berechnung des Ergebnisses einer Umfrage
 * und zum Erstellen einer Ergebnisübersicht
 */
@Service
public class UmfrageErgebnisService {
	
	private UmfrageAntwortRepository antwortRepo;
	
	public UmfrageErgebnisService(UmfrageAntwortRepository antwortRepo) {
		this.antwortRepo = antwortRepo;
	}
	
	/**
	 * Berechnet das Ergebnis einer Umfrage anhand der abgegebenen Antworten
	 * 
	 * @param umfrage Die Umfrage deren Ergebnis berechnet werden soll
	 * 
	 * @return Das Ergebnis der Umfrage
	 */
	public String berechneErgebnisUmfrage(Umfrage umfrage) {
		List<UmfrageAntwortDB> umfrageAntwortDBS =
			antwortRepo.findAllByUmfrageLink(umfrage.getLink());
		
		List<String> vorschlaege = umfrage.getVorschlaege();
		
		int[] ja = new int[vorschlaege.size()];
		int[] nein = new int[vorschlaege.size()];
		
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
		
		List<Integer> highest = IntegerToolkit.findHighestIndex(ja);
		
		return umfrage.getVorschlaege().get(highest.get(0));
	}
	
	/**
	 * Erstellt aus den Parametern eine ErgebnisForm, 
	 * die von dem UI benutzt werden kann
	 * 
	 * @param antworten Die zu der Umfrage gehörigen Antworten
	 * @param umfrage Die Umfrage, deren ErgebnisForm erstellt werden soll
	 * @param nutzerAbstimmung Die Antwort zu der Umfrage des aktuellen Nutzers
	 * 
	 * @return Die ErgebnisForm der Umfrage
	 */
	public ErgebnisFormUmfragen baueErgebnisForm(
		List<UmfrageAntwort> antworten,
		Umfrage umfrage,
		UmfrageAntwort nutzerAbstimmung) {
		
		ErgebnisFormUmfragen toReturn = new ErgebnisFormUmfragen();
		int anzahlAntworten;
		
		List<String> vorschlaege = new ArrayList<>();
		
		List<Antwort> nutzerAntworten = new ArrayList<>();
		
		List<Integer> anzahlStimmenJa = new ArrayList<>();
		
		List<Integer> anzahlStimmenNein = new ArrayList<>();
		
		List<Double> anteilStimmenJa = new ArrayList<>();
		
		List<Double> anteilStimmenNein = new ArrayList<>();
		
		List<Boolean> isNutzerAntwortJa = new ArrayList<>();
		
		List<String> jaAntwortPseudo = new ArrayList<>();
		
		List<String> neinAntwortPseudo = new ArrayList<>();
		
		boolean fristNichtAbgelaufen = false;
		
		LinkedHashMap<String, Antwort> nutzerAntwortenMap = nutzerAbstimmung.getAntworten();
		
		vorschlaege = umfrage.getVorschlaege();
		anzahlAntworten = antworten.size();
		
		LocalDateTime now = LocalDateTime.now();
		fristNichtAbgelaufen = !umfrage.getFrist().isBefore(now);
		
		for (String vorschlag : vorschlaege) {
			int ja = 0;
			int nein = 0;
			String jaAnt = "";
			String neinAnt = "";
			
			nutzerAntworten.add(nutzerAntwortenMap.get(vorschlag));
			for (UmfrageAntwort antwort : antworten) {
				LinkedHashMap<String, Antwort> antwortMap = antwort.getAntworten();
				Antwort a = antwortMap.get(vorschlag);
				String pseudonym = antwort.getPseudonym();
				if (a == Antwort.JA) {
					ja++;
					jaAnt = jaAnt + pseudonym + " ; ";
				} else if (a == Antwort.NEIN) {
					nein++;
					neinAnt = neinAnt + pseudonym + " ; ";
				}
				
			}
			
			anzahlStimmenJa.add(ja);
			anzahlStimmenNein.add(nein);
			jaAntwortPseudo.add(jaAnt);
			neinAntwortPseudo.add(neinAnt);
			
			double jaAnteil = 100 * (ja * 1.) / anzahlAntworten;
			double neinAnteil = 100 * (nein * 1.) / anzahlAntworten;
			anteilStimmenJa.add(jaAnteil);
			anteilStimmenNein.add(neinAnteil);
			
			isNutzerAntwortJa.add(nutzerAntwortenMap.get(vorschlag).equals(Antwort.JA));
		}
		
		
		toReturn.setAnzahlAntworten(anzahlAntworten);
		toReturn.setNutzerAntworten(nutzerAntworten);
		toReturn.setAnzahlStimmenJa(anzahlStimmenJa);
		toReturn.setAnzahlStimmenNein(anzahlStimmenNein);
		toReturn.setAnteilStimmenJa(anteilStimmenJa);
		toReturn.setAnteilStimmenNein(anteilStimmenNein);
		toReturn.setIsNutzerAntwortJa(isNutzerAntwortJa);
		toReturn.setJaAntwortPseudo(jaAntwortPseudo);
		toReturn.setNeinAntwortPseudo(neinAntwortPseudo);
		toReturn.setFristNichtAbgelaufen(fristNichtAbgelaufen);
		toReturn.setErgebnis(berechneErgebnisUmfrage(umfrage));
		toReturn.setVorschlaege(vorschlaege);
		
		return toReturn;
	}
}
