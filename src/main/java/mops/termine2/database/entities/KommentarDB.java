package mops.termine2.database.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDateTime;

@Entity
@Data
public class KommentarDB {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long kommentarId;
	
	private String pseudonym;
	
	private String link;
	
	private LocalDateTime erstellungsdatum;
	
	@Lob
	private String inhalt;
}
