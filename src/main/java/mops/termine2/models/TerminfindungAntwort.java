package mops.termine2.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mops.termine2.enums.Antwort;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TerminfindungAntwort {
	
	private String link;
	
	private String kuerzel;
	
	private LinkedHashMap<LocalDateTime, Antwort> antworten;
	
	private String pseudonym;
	
}
