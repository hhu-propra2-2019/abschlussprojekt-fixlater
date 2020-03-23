package mops.termine2.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
	
	private LocalDateTime loeschdatum;
	
	private Long gruppeId;
	
	private Long maxAntwortAnzahl;
	
	private String umfragenErgebnis;
	
}
