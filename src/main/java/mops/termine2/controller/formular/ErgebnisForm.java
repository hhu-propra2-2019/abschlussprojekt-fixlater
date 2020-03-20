package mops.termine2.controller.formular;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;
import mops.termine2.util.LocalDateTimeManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErgebnisForm {
	
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
	
	public ErgebnisForm(List<TerminfindungAntwort> antworten, Terminfindung terminfindung,
						TerminfindungAntwort nutzerAbstimmung) {
		HashMap<LocalDateTime, Antwort> nutzerAntwortenMap = nutzerAbstimmung.getAntworten();
		termine = terminfindung.getVorschlaege();
		LocalDateTimeManager.sortTermine(termine);
		anzahlAntworten = antworten.size();
		for (LocalDateTime localDateTime : termine) {
			int ja = 0;
			int nein = 0;
			int vielleicht = 0;
			
			
			nutzerAntworten.add(nutzerAntwortenMap.get(localDateTime));
			
			for (TerminfindungAntwort antwort : antworten) {
				HashMap<LocalDateTime, Antwort> antwortMap = antwort.getAntworten();
				Antwort a = antwortMap.get(localDateTime);
				if (a == Antwort.JA) {
					ja++;
				} else if (a == Antwort.NEIN) {
					nein++;
				} else {
					vielleicht++;
				}
				
			}
			termineString.add(LocalDateTimeManager.toString(localDateTime));
			anzahlStimmenJa.add(ja);
			anzahlStimmenNein.add(nein);
			anzahlStimmenVielleicht.add(vielleicht);
			
			double jaAnteil = 100 * (ja * 1.) / anzahlAntworten;
			double vielleichtAnteil = 100 * (vielleicht * 1.) / anzahlAntworten;
			double neinAnteil = 100 * (nein * 1.) / anzahlAntworten;
			anteilStimmenJa.add(jaAnteil);
			anteilStimmenVielleicht.add(vielleichtAnteil);
			anteilStimmenNein.add(neinAnteil);
			
			isNutzerAntwortJa.add(nutzerAntwortenMap.get(localDateTime).equals(Antwort.JA));
			isNutzerAntwortVielleicht.add(nutzerAntwortenMap.get(localDateTime).equals(Antwort.VIELLEICHT));
		}
	}
	
	
}

