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
import java.util.LinkedHashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AntwortForm {
	
	public List<LocalDateTime> termine = new ArrayList<>();
	
	public List<String> termineString = new ArrayList<>();
	
	public List<Antwort> antworten = new ArrayList<>();
	
	public String pseudonym;
	
	public String benutzer;
	
	
	public void init(TerminfindungAntwort terminAbstimmung) {
		LinkedHashMap<LocalDateTime, Antwort> antwortenMap = terminAbstimmung.getAntworten();
		pseudonym = terminAbstimmung.getPseudonym();
		benutzer = terminAbstimmung.getKuerzel();
		
		termine = new ArrayList<>();
		termine.addAll(antwortenMap.keySet());
		LocalDateTimeManager.sortTermine(termine);
		for (LocalDateTime termin : termine) {
			antworten.add(antwortenMap.get(termin));
			termineString.add(LocalDateTimeManager.toString(termin));
		}
	}
	
	
	public static TerminfindungAntwort mergeToAnswer(Terminfindung terminf, String nutzer, AntwortForm antwortFrm) {
		TerminfindungAntwort terminfindungAntwort = new TerminfindungAntwort();
		terminfindungAntwort.setKuerzel(nutzer);
		terminfindungAntwort.setLink(terminf.getLink());
		if (antwortFrm.getPseudonym().equals("")) {
			terminfindungAntwort.setPseudonym(nutzer);
		} else {
			terminfindungAntwort.setPseudonym(antwortFrm.getPseudonym());
		}
		
		List<LocalDateTime> termine = terminf.getVorschlaege();
		LocalDateTimeManager.sortTermine(termine);
		List<Antwort> antworten = antwortFrm.getAntworten();
		if (termine.size() != antworten.size()) {
			return null;
		}
		
		
		LinkedHashMap<LocalDateTime, Antwort> antwortenMap = new LinkedHashMap<>();
		for (int i = 0; i < termine.size(); i++) {
			antwortenMap.put(termine.get(i), antworten.get(i));
		}
		
		terminfindungAntwort.setAntworten(antwortenMap);
		return terminfindungAntwort;
	}
}
