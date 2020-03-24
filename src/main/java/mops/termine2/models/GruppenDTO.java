package mops.termine2.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GruppenDTO {
	
	private List<GruppeDTO> groupList;
	
	private Integer status;
	
}
