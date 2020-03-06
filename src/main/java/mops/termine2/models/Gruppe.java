package mops.termine2.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Gruppe {
	
	private Long id;
	
	private List<String> benutzer;
	
}
