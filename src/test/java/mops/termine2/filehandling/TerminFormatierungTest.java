package mops.termine2.filehandling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TerminFormatierungTest {
	
	private transient TerminFormatierung terminFormatierung;
	
	@BeforeEach
	public void setUp() {
		int anzahlTermine = 8;
		List<String[]> termineString = erstelleListeGueltigeTermineString(anzahlTermine);
		terminFormatierung = new TerminFormatierung(termineString);
	}
	
	@Test
	public void testMitDatumGueltigesFormat() {
		// Arrange in setUp()
		
		Boolean gueltigesFormat = terminFormatierung.pruefeObGueltigesFormat(
			terminFormatierung.getTermineEingelesen(), terminFormatierung.getDateTimeFormatter());
		
		assertThat(gueltigesFormat).isEqualTo(true);
	}
	
	@Test
	public void testMitDatumUngueltigesFormat() {
		String[] ungueltigesFormat = new String[] {"12-12-2020", "12:00"};
		terminFormatierung.getTermineEingelesen().set(2, ungueltigesFormat);
		
		Boolean gueltigesFormat = terminFormatierung.pruefeObGueltigesFormat(
			terminFormatierung.getTermineEingelesen(), terminFormatierung.getDateTimeFormatter());
		
		assertThat(gueltigesFormat).isEqualTo(false);
	}
	
	@Test
	public void testMitDatumUngueltigesFormatString() {
		String[] ungueltigesFormat = new String[] {"datum", "12:00"};
		terminFormatierung.getTermineEingelesen().set(3, ungueltigesFormat);
		
		Boolean gueltigesFormat = terminFormatierung.pruefeObGueltigesFormat(
			terminFormatierung.getTermineEingelesen(), terminFormatierung.getDateTimeFormatter());
		
		assertThat(gueltigesFormat).isEqualTo(false);
	}
	
	@Test
	public void testMitDatumExistent() {
		// Arrange in setUp()
		
		Boolean gueltigesDatum = terminFormatierung.pruefeObGueltigesDatum(
			terminFormatierung.getTermineEingelesen());
		
		assertThat(gueltigesDatum).isEqualTo(true);
	}
	
	@Test
	public void testMitDatumExistentSchaltjahr() {
		String[] ungueltigesDatumSchaltjahr = new String[] {"29.02.2024", "12:00"};
		terminFormatierung.getTermineEingelesen().set(0, ungueltigesDatumSchaltjahr);
		
		Boolean gueltigesDatum = terminFormatierung.pruefeObGueltigesDatum(
			terminFormatierung.getTermineEingelesen());
		
		assertThat(gueltigesDatum).isEqualTo(true);
	}
	
	@Test
	public void testMitDatumNichtExistent() {
		String[] ungueltigesDatum = new String[] {"29.02.2021", "12:00"};
		terminFormatierung.getTermineEingelesen().set(0, ungueltigesDatum);
		
		Boolean gueltigesDatum = terminFormatierung.pruefeObGueltigesDatum(
			terminFormatierung.getTermineEingelesen());
		
		assertThat(gueltigesDatum).isEqualTo(false);
	}
	
	@Test
	public void testMitDatumInZukunft() {
		String[] datumZukunft = new String[] {"30.12.2022", "12:00"};
		terminFormatierung.getTermineEingelesen().set(0, datumZukunft);
		
		Boolean datumInZukunft = terminFormatierung.pruefeObInZukunft(
			terminFormatierung.getTermineEingelesen(), terminFormatierung.getDateTimeFormatter());
		
		assertThat(datumInZukunft).isEqualTo(true);
	}
	
	@Test
	public void testMitDatumInVergangenheit() {
		String[] datumVergangen = new String[] {"02.03.2020", "12:00"};
		terminFormatierung.getTermineEingelesen().set(1, datumVergangen);
		
		Boolean datumInZukunft = terminFormatierung.pruefeObInZukunft(
			terminFormatierung.getTermineEingelesen(), terminFormatierung.getDateTimeFormatter());
		
		assertThat(datumInZukunft).isEqualTo(false);
	}
	
	private List<String[]> erstelleListeGueltigeTermineString(int anzahlTermine) {
		List<String[]> termineString = new ArrayList<>();
		for (int i = 1; i <= anzahlTermine; i++) {
			termineString.add(new String[] {"12.0" + i + ".2021", "12:00"});
		}
		return termineString;
	}
	
}
