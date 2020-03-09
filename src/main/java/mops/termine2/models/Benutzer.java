package mops.termine2.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class Benutzer {
	
	private String kuerzel;
	
	private List<Gruppe> gruppe; // Strings, int?
	
	private List<String> linksErstellt;
	
	private List<String> linksTeilgenommen;
	
	private List<String> linksNochNichtTeilgenommen;
	
}
