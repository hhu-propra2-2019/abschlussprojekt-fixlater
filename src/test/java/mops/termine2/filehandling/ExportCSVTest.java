package mops.termine2.filehandling;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExportCSVTest {

	private transient ExportCSV exportCSV;
	
	private LocalDateTime festerTermin = LocalDateTime.of(2020, 3, 13, 13, 13);
	
	@Test
	public void localDateTimeZuExportFormatNurGueltigeTerminVorSchlaege() {
		int anzahl = 3;
		List<LocalDateTime> terminVorschlaege = erstelleGueltigeTerminListe(anzahl);
		exportCSV = new ExportCSV(terminVorschlaege);
		List<ExportFormat> exportFormat = erstelleTermineImExportFormat(anzahl);
		
		List<ExportFormat> terminVorschlaegeExportFormat = exportCSV.localDateTimeZuExportFormat();
		
		assertThat(terminVorschlaegeExportFormat).isEqualTo(exportFormat);
		
		
	}
	
	@Test
	public void localDateTimeZuExportFormatNullEintrag() {
		int anzahl = 4;
		List<LocalDateTime> terminVorschlaege = erstelleGueltigeTerminListe(anzahl);
		terminVorschlaege.add(null);
		exportCSV = new ExportCSV(terminVorschlaege);
		List<ExportFormat> exportFormat = erstelleTermineImExportFormat(anzahl);
		
		List<ExportFormat> terminVorschlaegeExportFormat = exportCSV.localDateTimeZuExportFormat();
		
		assertThat(terminVorschlaegeExportFormat).isEqualTo(exportFormat);
	}
	
	
	
	public List<LocalDateTime> erstelleGueltigeTerminListe(int anzahl) {
		List<LocalDateTime> terminVorschlaege = new ArrayList<>();
		for (int i = 0; i < anzahl; i++) {
			terminVorschlaege.add(festerTermin.plusDays(i));
		}
		return terminVorschlaege;
	}
	
	public List<ExportFormat> erstelleTermineImExportFormat(int anzahl) {
		List<ExportFormat> terminVorschlaegeExportFormat = new ArrayList<>();
		for (int i = 13; i < 13 + anzahl; i++) {
			terminVorschlaegeExportFormat.add(new ExportFormat(i + ".03.2020", "13:13"));
		}
		return terminVorschlaegeExportFormat;
	}
}



