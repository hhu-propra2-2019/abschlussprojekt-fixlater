package mops.termine2.controller.formular;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Umfrage;
import mops.termine2.models.UmfrageAntwort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErgebnisFormUmfragen {
	
	int anzahlAntworten;
	
	List<String> vorschlaege = new ArrayList<>();
	
	List<Antwort> nutzerAntworten = new ArrayList<>();
	
	List<Integer> anzahlStimmenJa = new ArrayList<>();
	
	List<Integer> anzahlStimmenNein = new ArrayList<>();
	
	List<Double> anteilStimmenJa = new ArrayList<>();
	
	List<Double> anteilStimmenNein = new ArrayList<>();
	
	List<Boolean> isNutzerAntwortJa = new ArrayList<>();
	
	List<String> jaAntwortPseud = new ArrayList<>();
	
	List<String> neinAntwortPseudo = new ArrayList<>();
	
	boolean fristNichtAbgelaufen = false;
	
	String ergebnis = "ein Vorschlag";
	
	public ErgebnisFormUmfragen(List<UmfrageAntwort> antworten, Umfrage umfrage,
								UmfrageAntwort nutzerAbstimmung) {
		HashMap<String, Antwort> nutzerAntwortenMap = nutzerAbstimmung.getAntworten();
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
				HashMap<String, Antwort> antwortMap = antwort.getAntworten();
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
			jaAntwortPseud.add(jaAnt);
			neinAntwortPseudo.add(neinAnt);
			
			double jaAnteil = 100 * (ja * 1.) / anzahlAntworten;
			double neinAnteil = 100 * (nein * 1.) / anzahlAntworten;
			anteilStimmenJa.add(jaAnteil);
			anteilStimmenNein.add(neinAnteil);
			
			isNutzerAntwortJa.add(nutzerAntwortenMap.get(vorschlag).equals(Antwort.JA));
		}
	}
	
	
}

