package mops.termine2.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Umfrageuebersicht {
	
	private List<Umfrage> teilgenommen;
	
	private List<Umfrage> offen;
	
	private List<String> gruppen;
	
}
