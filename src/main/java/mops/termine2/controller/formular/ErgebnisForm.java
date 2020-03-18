package mops.termine2.controller.formular;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErgebnisForm {
	
	List<LocalDateTime> termine = new ArrayList<>();
	
	List<Integer> anzahlStimmenJa = new ArrayList<>();
	
	List<Integer> anzahlStimmenVielleicht = new ArrayList<>();
	
	List<Integer> anzahlStimmenNein = new ArrayList<>();
	
	
}

