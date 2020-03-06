package mops.termine2.database.entities;

import lombok.Data;
import mops.termine2.enums.Antwort;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
public class TerminfindungAntwort {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long terminfindungAntwortId;
	
	private String benutzer;
	
	@ManyToOne
	private Terminfindung terminfindung;
	
	private Antwort antwort;
}
