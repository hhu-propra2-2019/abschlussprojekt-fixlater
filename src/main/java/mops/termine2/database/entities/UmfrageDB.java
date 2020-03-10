package mops.termine2.database.entities;

import lombok.Data;
import mops.termine2.enums.Modus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
public class UmfrageDB {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long umfrageId;
	
	private String auswahlmoeglichkeit;
	
	private String link;
	
	private LocalDateTime frist;
	
	private LocalDateTime loeschdatum;
	
	private String ersteller;
	
	private String titel;
	
	private Modus modus;
	
	private String beschreibung;
	
	private String gruppe;
	
	private Long maxAntwortAnzahl;
	
}
