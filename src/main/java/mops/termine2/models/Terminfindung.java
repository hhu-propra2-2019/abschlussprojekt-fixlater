package mops.termine2.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@EqualsAndHashCode
public class Terminfindung {
	
	private String link;
	
	private String titel;
	
	private String ort;
	
	private String beschreibung;
	
	@DateTimeFormat(pattern = "dd.MM.yyyy, HH:mm")
	private List<LocalDateTime> vorschlaege;
	
	private String ersteller;
	
	@DateTimeFormat(pattern = "dd.MM.yyyy, HH:mm")
	private LocalDateTime frist;
	
	private LocalDateTime loeschdatum;
	
	private String gruppe;
	
	private LocalDateTime ergebnis;
	
}
