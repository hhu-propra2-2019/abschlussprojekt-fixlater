package mops.termine2.models;

import lombok.Getter;
import lombok.Setter;
import mops.termine2.enums.Antwort;

import java.time.LocalDateTime;
import java.util.HashMap;

@Getter
@Setter
public class Teilnahme {
	
	private String link;
	
	private Long gruppeId;
	
	private String kuerzel;
	
	private HashMap<LocalDateTime, Antwort> antworten;
	
	private String pseudonym;
	
	private Boolean teilgenommen;
	
}
