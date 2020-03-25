package mops.termine2.filehandling;

import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
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
	
	public Boolean pruefeObGueltigesFormat(
		List<String[]> termineEingelesen, DateTimeFormatter formatter) {
		try {
			for (String[] terminEingelesen : termineEingelesen) {
				LocalDateTime.parse(terminEingelesen[0]
					+ ", " + terminEingelesen[1], formatter);
			}
			return true;
		} catch (Exception ex) {
			return false;
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
	
	public Boolean pruefeObGueltigesDatum(List<String[]> termineEingelesen) {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy,HH:mm");
		format.setLenient(false);
		
		try {
			for (String[] terminEingelesen : termineEingelesen) {
				format.parse(terminEingelesen[0] + "," + terminEingelesen[1]);
			}
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
}
