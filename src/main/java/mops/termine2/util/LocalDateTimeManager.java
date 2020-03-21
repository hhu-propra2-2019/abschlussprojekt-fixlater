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
		int monthZiffer0 = IntegerToolkit.getFirstZiffer(month);
		int monthZiffer1 = IntegerToolkit.getSecondZiffer(month);
		int dayZiffer0 = IntegerToolkit.getFirstZiffer(dayOfMonth);
		int dayZiffer1 = IntegerToolkit.getSecondZiffer(dayOfMonth);
		int minuteZiffer0 = IntegerToolkit.getFirstZiffer(time.getMinute());
		int minuteZiffer1 = IntegerToolkit.getSecondZiffer(time.getMinute());
		int stundeZiffer0 = IntegerToolkit.getFirstZiffer(time.getHour());
		int stundeZiffer1 = IntegerToolkit.getSecondZiffer(time.getHour());
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

