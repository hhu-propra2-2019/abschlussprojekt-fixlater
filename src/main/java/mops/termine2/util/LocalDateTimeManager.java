package mops.termine2.util;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
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
	
	public static String toString(LocalDateTime time) {
		int dayOfWeek = time.getDayOfWeek().getValue();
		int dayOfMonth = time.getDayOfMonth();
		Month month = time.getMonth();
		int year = time.getYear();
		
		String weekday;
		if (dayOfWeek == 0) {
			weekday = "So.";
		} else if (dayOfWeek == 1) {
			weekday = "Mo.";
		} else if (dayOfWeek == 2) {
			weekday = "Di.";
		} else if (dayOfWeek == 3) {
			weekday = "Mi.";
		} else if (dayOfWeek == 4) {
			weekday = "Do.";
		} else if (dayOfWeek == 5) {
			weekday = "Fr.";
		} else {
			weekday = "Sa.";
		}
		
		String datum = weekday + " " + dayOfMonth + "." + month.getValue() + "." + year;
		String uhrzeit = time.getHour() + ":" + time.getMinute();
		return datum + " " + uhrzeit;
	}
	
	public static List<String> toString(List<LocalDateTime> times) {
		List<String> result = new ArrayList<>();
		for (LocalDateTime time : times) {
			result.add(toString(time));
		}
		return result;
	}
	
}
