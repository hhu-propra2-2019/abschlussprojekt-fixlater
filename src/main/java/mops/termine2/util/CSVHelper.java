package mops.termine2.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import mops.termine2.filehandling.ExportCSV;
import mops.termine2.filehandling.ExportFormat;
import mops.termine2.filehandling.TerminFormatierung;
import mops.termine2.models.Terminfindung;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

public class CSVHelper {
	
	public static List<String> readCSV(MultipartFile file, 
		Terminfindung terminfindung, List<LocalDateTime> termine) {
		List<String> fehler = new ArrayList<String>();
		if (file.isEmpty()) {
			fehler.add("Bitte eine CSV-Datei zum Upload auswählen.");
		} else {
			try (CSVReader csvReader = new CSVReader(
				new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
				
				List<String[]> termineEingelesen = csvReader.readAll();
				if ("DATUM".equals(termineEingelesen.get(0)[0])) {
					termineEingelesen.remove(0);
				}
				
				TerminFormatierung terminFormatierung = new TerminFormatierung(termineEingelesen);
				
				if (!terminFormatierung.pruefeObGueltigesFormat()) {
					fehler.add(
						"Alle Termine müssen im Format "
							+ "'TT.MM.JJJJ,HH:MM' übergeben werden und "
							+ "sollten existente Daten sein.");
				} else if (!terminFormatierung.pruefeObInZukunft()) {
					fehler.add("Die Termine sollten in der Zukunft liegen.");
					
				} else if (!terminFormatierung.pruefeObGueltigesDatum()) {
					fehler.add("Die Termine sollten existieren.");					
				} else {
					// entferne ggf. überflüssige Datumsbox
					if (termine.get(0) == null) {
						terminfindung.getVorschlaege().remove(0);
					}
					
					// füge Termine ins Model ein
					for (String[] terminEingelesen : termineEingelesen) {
						LocalDateTime termin = LocalDateTime.parse(terminEingelesen[0]
							+ ", " + terminEingelesen[1],
							terminFormatierung
								.getDateTimeFormatter());
						termine.add(termin);
					}
				}
				
			} catch (RuntimeException ex) {
				throw ex;
			} catch (Exception ex) {
				fehler.add("Ein Fehler ist beim Verarbeiten der CSV-Datei aufgetreten.");
			}
		}
		return fehler;
	}
	
	public static void exportCSV(Terminfindung terminfindung, HttpServletResponse response) 
		throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
			"attachment; filename=\"termine.csv\"");
		response.setContentType("text/csv");
		
		List<LocalDateTime> termine = terminfindung.getVorschlaege();
		
		if (termine.get(0) != null) {
			StatefulBeanToCsv<ExportFormat> writer = new StatefulBeanToCsvBuilder<ExportFormat>(
				response.getWriter()).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
					.withOrderedResults(false)
					.build();
			writer.write(new ExportCSV(termine).localDateTimeZuExportFormat());
		}
		
	}
	
}
