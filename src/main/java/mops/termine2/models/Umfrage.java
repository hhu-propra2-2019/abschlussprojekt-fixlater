package mops.termine2.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Umfrage {
	
	private String link;
	
	private String titel;
	
	private String beschreibung;
	
	private List<String> vorschlaege;
	
	private String ersteller;
	
	private LocalDateTime frist;
	
	private LocalDateTime loeschdatum;
	
	private String gruppe;
	
}
