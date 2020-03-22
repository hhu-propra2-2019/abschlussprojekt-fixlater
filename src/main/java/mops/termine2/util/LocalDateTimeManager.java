package mops.termine2.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocalDateTimeManager {
	
	
	public static void sortTermine(List<LocalDateTime> termine) {
		Collections.sort(termine);
	}
	
	public static String toString(LocalDateTime time) {
		int dayOfWeek = time.getDayOfWeek().getValue();
		int dayOfMonth = time.getDayOfMonth();
		int month = time.getMonth().getValue();
		int year = time.getYear();
		
		String weekday;
		switch (dayOfWeek) {
		case 0:
			weekday = "So.";
			break;
		case 1:
			weekday = "Mo.";
			break;
		case 2:
			weekday = "Di.";
			break;
		case 3:
			weekday = "Mi.";
			break;
		case 4:
			weekday = "Do.";
			break;
		case 5:
			weekday = "Fr.";
			break;
		default:
			weekday = "Sa.";
			
			
		}
		
		//um führende NKullen anzuzeigen
		int monthZiffer0 = IntegerToolkit.getZiffer(0, month);
		int monthZiffer1 = IntegerToolkit.getZiffer(1, month);
		int dayZiffer0 = IntegerToolkit.getZiffer(0, dayOfMonth);
		int dayZiffer1 = IntegerToolkit.getZiffer(1, dayOfMonth);
		int minuteZiffer0 = IntegerToolkit.getZiffer(0, time.getMinute());
		int minuteZiffer1 = IntegerToolkit.getZiffer(1, time.getMinute());
		int stundeZiffer0 = IntegerToolkit.getZiffer(0, time.getHour());
		int stundeZiffer1 = IntegerToolkit.getZiffer(1, time.getHour());
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

