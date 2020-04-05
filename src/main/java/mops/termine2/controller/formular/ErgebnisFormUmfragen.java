package mops.termine2.controller.formular;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mops.termine2.enums.Antwort;

import java.util.ArrayList;
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
	
	List<String> jaAntwortPseudo = new ArrayList<>();
	
	List<String> neinAntwortPseudo = new ArrayList<>();
	
	boolean fristNichtAbgelaufen = false;
	
	String ergebnis = "ein Vorschlag";
	
}

