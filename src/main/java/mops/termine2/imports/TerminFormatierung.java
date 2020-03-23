package mops.termine2.imports;

import lombok.Getter;

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
	
	public void pruefeFuerJedenTerminGueltigesFormat(
		List<String[]> termineEingelesen, DateTimeFormatter formatter) {
		for (String[] terminEingelesen : termineEingelesen) {
			LocalDateTime.parse(terminEingelesen[0]
				+ ", " + terminEingelesen[1], formatter);
		}
	}
	
}
