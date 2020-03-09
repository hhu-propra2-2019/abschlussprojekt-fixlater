package mops.termine2.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;

@Getter
@Setter
public class Teilnahme {
	
	private String link;
	
	private Long gruppeId;
	
	private String kuerzel;
	
	private HashMap<Date, Antwort> antworten;
	
	private String pseudonym;
	
	private Boolean teilgenommen;
	
}
