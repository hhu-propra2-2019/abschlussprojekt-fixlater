package mops.termine2.util;

import java.time.LocalDateTime;
import java.util.List;

public class LocalDateTimeManager {
	
	public static void sortTermine(List<LocalDateTime> termine) {
		
		for (int i = 0; i < termine.size(); i++) {
			for (int j = i; j < termine.size(); j++) {
				if (termine.get(i).isAfter(termine.get(j))) {
					LocalDateTime tmpTermin = termine.get(j);
					termine.set(j, termine.get(i));
					termine.set(i, tmpTermin);
				}
			}
		}
	}
}
