package mops.termine2.util;

import java.time.LocalDateTime;
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
		int month = time.getMonth().getValue();
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
		
		int monthZiffer0 = month % 10;
		int monthZiffer1 = month / 10 % 10;
		int dayZiffer0 = dayOfMonth % 10;
		int dayZiffer1 = dayOfMonth / 10 % 10;
		int minuteZiffer0 = time.getMinute() % 10;
		int minuteZiffer1 = time.getMinute() / 10 % 10;
		int stundeZiffer0 = time.getHour() % 10;
		int stundeZiffer1 = time.getHour() / 10 % 10;
		String datum = weekday + " "
			+ dayZiffer1 + "" + dayZiffer0 + "." + monthZiffer1 + "" + monthZiffer0 + "." + year;
		String uhrzeit = stundeZiffer1 + "" + stundeZiffer0 + ":" + minuteZiffer1 + "" + minuteZiffer0;
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

