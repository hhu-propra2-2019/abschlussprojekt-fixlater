package mops.termine2.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BenutzerDTO {
	
	private String email;
	
	private String familyname;
	
	private String givenname;
	
	private String /* CHECKSTYLE:OFF */ user_id; /* CHECKSTYLE:ON */
	
}
