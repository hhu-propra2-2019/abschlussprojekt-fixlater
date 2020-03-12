package mops.termine2.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode
public class Terminfindung {
	
	private String link;
	
	private String titel;
	
	private String ort;
	
	private String beschreibung;
	
	private List<LocalDateTime> vorschlaege;
	
	private String ersteller;
	
	private LocalDateTime frist;
	
	private LocalDateTime loeschdatum;
	
	private String gruppe;
	
}
