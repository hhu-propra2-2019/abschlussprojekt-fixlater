package mops.termine2.controller.formular;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mops.termine2.enums.Antwort;
import mops.termine2.models.TerminfindungAntwort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AntwortForm {
	
	public List<LocalDateTime> termine = new ArrayList<>();
	
	public List<Antwort> antworten = new ArrayList<>();
	
	public String pseudonym;
	
	public String benutzer;
	
	
	public void init(TerminfindungAntwort terminAntwort) {
		pseudonym = terminAntwort.getPseudonym();
		benutzer = terminAntwort.getKuerzel();
		save(terminAntwort.getAntworten());
		sort();
	}
	
	
	private void save(HashMap<LocalDateTime, Antwort> antwortMap) {
		for (LocalDateTime termin : antwortMap.keySet()) {
			termine.add(termin);
			antworten.add(antwortMap.get(termin));
		}
	}
	
	//Wenn man lustig ist effizienteren Algorithmus verwenden
	private void sort() {
		for (int i = 0; i < termine.size(); i++) {
			for (int j = i; j < termine.size(); j++) {
				if (termine.get(i).isAfter(termine.get(j))) {
					LocalDateTime tmpTermin = termine.get(j);
					Antwort tmpAntwort = antworten.get(j);
					termine.set(j, termine.get(i));
					termine.set(i, tmpTermin);
					antworten.set(j, antworten.get(i));
					antworten.set(i, tmpAntwort);
				}
			}
		}
	}
}
