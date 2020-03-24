package mops.termine2.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Umfrageuebersicht {
	
	private List<Umfrage> abgeschlossen;
	
	private List<Umfrage> offen;
	
	private List<Gruppe> gruppen;
	
	private Gruppe selektierteGruppe;
	
}
