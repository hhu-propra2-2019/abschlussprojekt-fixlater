package mops.termine2.models;

import lombok.Getter;
import lombok.Setter;
import mops.termine2.enums.Antwort;

import java.time.LocalDateTime;
import java.util.HashMap;

@Getter
@Setter
public class TerminfindungAntwort {
	
	private String link;
	
	private String gruppe;
	
	private String kuerzel;
	
	private HashMap<LocalDateTime, Antwort> antworten;
	
	private String pseudonym;
	
	// Brauchen wir vermutlich nicht
	private Boolean teilgenommen;
	
}
