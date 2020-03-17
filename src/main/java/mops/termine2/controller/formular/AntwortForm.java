package mops.termine2.controller.formular;

import mops.termine2.enums.Antwort;
import mops.termine2.models.TerminfindungAntwort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AntwortForm {
	
	private List<LocalDateTime> termine = new ArrayList<>();
	
	private List<Antwort> antworten = new ArrayList<>();
	
	private String pseudonym;
	
	public AntwortForm(TerminfindungAntwort terminAntwort) {
		pseudonym = terminAntwort.getPseudonym();
		save(terminAntwort.getAntworten());
		sort();
	}
	
	private void save(HashMap<LocalDateTime, Antwort> antwortMap) {
		for (LocalDateTime termin : antwortMap.keySet()) {
			termine.add(termin);
			antworten.add(antwortMap.get(termin));
		}
	}
	
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
