package mops.termine2.models;

import java.util.HashMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import mops.termine2.enums.Antwort;

@Getter
@Setter
@EqualsAndHashCode
public class UmfrageAntwort {
	
	private String link;
	
	private String gruppe;
	
	private String benutzer;
	
	private HashMap<String, Antwort> antworten;
	
	private String pseudonym;
	
	private Boolean teilgenommen;
}
