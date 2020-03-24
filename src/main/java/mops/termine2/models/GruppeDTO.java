package mops.termine2.models;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GruppeDTO {
	
	private String description;
	
	private Integer id;
	
	private List<BenutzerDTO> members;
	
	private Integer parent;
	
	private HashMap<String, String> roles;
	
	private String title;
	
	private String type;
	
	private String visibility;
	
}
