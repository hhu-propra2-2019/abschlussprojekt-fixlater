package mops.termine2.controller.formular;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Umfrage;
import mops.termine2.models.UmfrageAntwort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AntwortFormUmfragen {
	
	public List<String> vorschlaege = new ArrayList<>();
	
	//public List<String> termineString = new ArrayList<>();
	
	public List<Antwort> antworten = new ArrayList<>();
	
	public String pseudonym;
	
	public String benutzer;
	
	@SuppressWarnings("checkstyle:LineLength")
	public static UmfrageAntwort mergeToAnswer(Umfrage umfrage, String nutzer,
		AntwortFormUmfragen antwortFrm) {
		UmfrageAntwort umfrageAntwort = new UmfrageAntwort();
		umfrageAntwort.setBenutzer(nutzer);
		umfrageAntwort.setLink(umfrage.getLink());
		if (antwortFrm.getPseudonym().equals("")) {
			umfrageAntwort.setPseudonym(nutzer);
		} else {
			umfrageAntwort.setPseudonym(antwortFrm.getPseudonym());
		}
		
		List<String> vorschlaege = umfrage.getVorschlaege();
		List<Antwort> antworten = antwortFrm.getAntworten();
		if (vorschlaege.size() != antworten.size()) {
			return null;
		}
		
		HashMap<String, Antwort> antwortenMap = new HashMap<>();
		for (int i = 0; i < vorschlaege.size(); i++) {
			antwortenMap.put(vorschlaege.get(i), antworten.get(i));
		}
		
		umfrageAntwort.setAntworten(antwortenMap);
		return umfrageAntwort;
	}
	
	public void init(UmfrageAntwort umfrageAntwort) {
		HashMap<String, Antwort> antwortenMap = umfrageAntwort.getAntworten();
		pseudonym = umfrageAntwort.getPseudonym();
		benutzer = umfrageAntwort.getBenutzer();
		
		vorschlaege = new ArrayList<>();
		vorschlaege.addAll(antwortenMap.keySet());
		for (String vorschlag : vorschlaege) {
			antworten.add(antwortenMap.get(vorschlag));
			//termineString.add(LocalDateTimeManager.toString(termin));
		}
	}
}
