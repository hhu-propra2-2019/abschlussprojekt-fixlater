package mops.termine2.database.entities;

import lombok.Data;
import mops.termine2.enums.Modus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDateTime;

@Entity
@Data
public class TerminfindungDB {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long terminfindungId;
	
	private LocalDateTime termin;
	
	private String ort;
	
	private String link;
	
	private LocalDateTime frist;
	
	private LocalDateTime loeschdatum;
	
	private String ersteller;
	
	private String titel;
	
	private Modus modus;
	
	@Lob
	private String beschreibung;
	
	private String gruppeId;
	
	private LocalDateTime ergebnis;
	
	private Boolean ergebnisVorFrist;
	
	private Boolean einmaligeAbstimmung;
}

