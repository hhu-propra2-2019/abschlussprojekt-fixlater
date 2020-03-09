package mops.termine2.database.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class GruppeDB {
	
	@Id
	private Long gruppeId;
	
	private String name;
}
