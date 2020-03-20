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
	
	List<LocalDateTime> termine = new ArrayList<>();
	
	List<Integer> anzahlStimmenJa = new ArrayList<>();
	
	List<Integer> anzahlStimmenVielleicht = new ArrayList<>();
	
	List<Integer> anzahlStimmenNein = new ArrayList<>();
	
	public ErgebnisForm(List<TerminfindungAntwort> antworten, Terminfindung terminfindung) {
		termine = terminfindung.getVorschlaege();
		LocalDateTimeManager.sortTermine(termine);
		for (LocalDateTime localDateTime : termine) {
			int ja = 0;
			int nein = 0;
			int vielleicht = 0;
			
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
			
			anzahlStimmenJa.add(ja);
			anzahlStimmenNein.add(nein);
			anzahlStimmenVielleicht.add(vielleicht);
		}
	}
}

