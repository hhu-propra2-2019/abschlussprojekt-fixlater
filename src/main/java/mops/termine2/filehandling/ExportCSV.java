package mops.termine2.filehandling;

import com.opencsv.CSVWriter;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ExportCSV {
	public DateTimeFormatter dateTimeFormatter;
	
	public List<LocalDateTime> terminVorschlaege;
	
	public ExportCSV(List<LocalDateTime> terminVorschlaege) {
		this.dateTimeFormatter = setzeFormatFuerTermine();
		this.terminVorschlaege = terminVorschlaege;
	}
	
	public DateTimeFormatter setzeFormatFuerTermine() {
		DateTimeFormatterBuilder b = new DateTimeFormatterBuilder();
		return b.appendPattern("dd.MM.")
				.appendValue(ChronoField.YEAR_OF_ERA, 4, 4,
						SignStyle.EXCEEDS_PAD).appendPattern(",HH:mm")
				.toFormatter();
	}
	
	public void localDateTimeZuString(CSVWriter writer) {
		List<String[]> stringTermine = new ArrayList<>();
		for (LocalDateTime localDatetermin : this.getTerminVorschlaege()) {
			String termin = localDatetermin.format(this.getDateTimeFormatter());
			System.out.println(termin);
			writer.writeNext(termin.split(","));
		}
	}
	
}
