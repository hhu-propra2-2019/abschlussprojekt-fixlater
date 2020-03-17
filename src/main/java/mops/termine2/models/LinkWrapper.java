package mops.termine2.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LinkWrapper {
	
	String link;
	
	public LinkWrapper(String link) {
		this.link = link;
	}
}
