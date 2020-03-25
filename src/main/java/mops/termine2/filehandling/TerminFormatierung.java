package mops.termine2.filehandling;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TerminFormatierung {
	
	public DateTimeFormatter dateTimeFormatter;
	
	public List<String[]> termineEingelesen;
	
	public TerminFormatierung(List<String[]> termineEingelesen) {
		this.dateTimeFormatter = setzeFormatFuerTermine();
		this.termineEingelesen = termineEingelesen;
	}
	
	
	public DateTimeFormatter setzeFormatFuerTermine() {
		DateTimeFormatterBuilder b = new DateTimeFormatterBuilder();
		return b.appendPattern("dd.MM.")
			.appendValue(ChronoField.YEAR_OF_ERA, 4, 4,
				SignStyle.EXCEEDS_PAD).appendPattern(", HH:mm")
			.toFormatter();
	}
	
	public void pruefeFuerJedenTerminGueltigesFormat(
		List<String[]> termineEingelesen, DateTimeFormatter formatter) {
		for (String[] terminEingelesen : termineEingelesen) {
			LocalDateTime.parse(terminEingelesen[0]
				+ ", " + terminEingelesen[1], formatter);
		}
	}
	
	public Boolean pruefeObInZukunft(List<String[]> termineEingelesen, DateTimeFormatter formatter) {
		for (String[] terminEingelesen : termineEingelesen) {
			LocalDateTime zeit = LocalDateTime.parse(terminEingelesen[0]
					+ ", " + terminEingelesen[1], formatter);
			if (zeit.isBefore(LocalDateTime.now())) {
				return false;
			}
		}
		return true;
	}
	
	public Boolean pruefeObExistent(List<String[]> termineEingelesen) {
		DateFormat format = new SimpleDateFormat("dd.MM.yyyy,HH:mm");
		
		// Input to be parsed should strictly follow the defined date format
		// above.
		format.setLenient(false);
		
		try {
			for (String[] terminEingelesen : termineEingelesen) {
				format.parse(terminEingelesen[0] + "," + terminEingelesen[1]);
			}
			return true;
		} catch (ParseException e) {
			
			System.out.println("Date  is not valid according to "
					+ ((SimpleDateFormat) format).toPattern() + " pattern.");
			return false;
		}
	}
	
}
