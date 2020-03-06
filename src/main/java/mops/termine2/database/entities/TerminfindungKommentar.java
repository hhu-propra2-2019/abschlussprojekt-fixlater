package mops.termine2.database.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Data
public class TerminfindungKommentar {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long kommentarId;
	
	private String pseudonym;
	
	@ManyToOne
	private Terminfindung terminfindung;
	
	private Date erstellungsdatum;
	
	private String inhalt;
}
