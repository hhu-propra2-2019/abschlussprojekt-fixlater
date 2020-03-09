package mops.termine2.database.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Data
public class Kommentar {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long kommentarId;
	
	private String pseudonym;
	
	@ManyToOne
	private Terminfindung terminfindung;
	
	private LocalDateTime erstellungsdatum;
	
	private String inhalt;
}
