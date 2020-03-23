package mops.termine2.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Terminuebersicht {
	
	private List<Terminfindung> abgeschlossen;
	
	private List<Terminfindung> offen;
	
	private List<Gruppe> gruppen;
	
	private Gruppe selektierteGruppe;
	
}
