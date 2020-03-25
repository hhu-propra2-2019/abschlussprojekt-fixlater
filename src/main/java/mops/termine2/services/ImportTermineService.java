package mops.termine2.services;


import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
public class ImportTermineService {
	
	@CsvDate("dd.MM.yyyy")
	public LocalDate datum;
	
	@CsvDate("HH:mm")
	public LocalTime zeit;
	
	
}
