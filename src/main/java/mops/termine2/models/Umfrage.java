package mops.termine2.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class Umfrage {
	
	private String link;
	
	private String titel;
	
	private String beschreibung;
	
	private List<String> vorschlaege;
	
	private String ersteller;
	
	@DateTimeFormat(pattern = "dd.MM.yyyy, HH:mm")
	private LocalDateTime frist;
	
	@DateTimeFormat(pattern = "dd.MM.yyyy, HH:mm")
	private LocalDateTime loeschdatum;
	
	private String gruppeId;
	
	private String gruppeName;
	
	private Long maxAntwortAnzahl;
	
	private String umfragenErgebnis;
	
	private boolean teilgenommen = false;
	
	private String ergebnis;
	
}
