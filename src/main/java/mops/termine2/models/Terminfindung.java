package mops.termine2.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Terminfindung {
	
	private String link;
	
	private String titel;
	
	private String ort;
	
	private String beschreibung;
	
	private List<Date> vorschlaege;
	
	private String ersteller;
	
	private Date frist;
	
	private Date loeschdatum;
	
	private Long gruppeId;
	
}
