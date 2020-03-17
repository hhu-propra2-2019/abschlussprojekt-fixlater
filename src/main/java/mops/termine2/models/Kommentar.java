package mops.termine2.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class Kommentar {
	
	private String link;
	
	private String inhalt;
	
	private String pseudonym;
	
	private LocalDateTime erstellungsdatum;
	
}
