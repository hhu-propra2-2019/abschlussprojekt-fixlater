package mops.termine2.controller.formular;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mops.termine2.enums.Antwort;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
	
	List<String> jaAntwortPseudo = new ArrayList<>();
	
	List<String> vielleichtAntwortPseudo = new ArrayList<>();
	
	List<String> neinAntwortPseudo = new ArrayList<>();
	
	boolean fristNichtAbgelaufen = false;
	
	String ergebnis = "eine Zeit";
	
	
}

