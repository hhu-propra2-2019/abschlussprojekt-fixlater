package mops.termine2.filehandling;

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
	
	public List<ExportFormat> localDateTimeZuExportFormat() {
		List<ExportFormat> termineExportFormat = new ArrayList<>();
		for (LocalDateTime localDatetermin : this.getTerminVorschlaege()) {
			if (localDatetermin != null) {
				String termin = localDatetermin.format(this.getDateTimeFormatter());
				String[] datumUndUhrzeit = termin.split(",");
				termineExportFormat.add(new ExportFormat(datumUndUhrzeit[0], datumUndUhrzeit[1]));
			}
		}
		return termineExportFormat;
	}
	
}
