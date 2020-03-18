package mops.termine2;

import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;
import mops.termine2.services.TerminAntwortService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class TerminfindungAntwortServiceTest {
	
	public static final String LINK = "IchBinKeinMannFuerEineNacht,IchBleibeHoechstensEinZweiStunden";
	
	public static final String BENUTZER1 = "Julia";
	
	private transient TerminfindungAntwortRepository antwortRepo;
	
	private transient TerminAntwortService antwortService;
	
	private transient TerminfindungRepository terminRepo;
	
	@BeforeEach
	private void setUp() {
		antwortRepo = mock(TerminfindungAntwortRepository.class);
		terminRepo = mock(TerminfindungRepository.class);
		antwortService = new TerminAntwortService(antwortRepo, terminRepo);
	}
	
	@Test
	public void saveAntwortFuerTerminfindungMit4Moeglichkeiten() {
		Terminfindung terminfindung = getBeispielTerminfindung();
		TerminfindungAntwort toSave = new TerminfindungAntwort();
		
		toSave.setAntworten(getBeispielAntwortenAlleJa(4));
		toSave.setKuerzel(BENUTZER1);
		when(terminRepo.findByLinkAndTermin(any(), any())).thenReturn(getBeispielTerminDBList(1).get(0));
		antwortService.abstimmen(toSave, terminfindung);
		
		Mockito.verify(antwortRepo, times(4)).save(any());
		
	}
	
	@Test
	public void saveAntwortFuerTerminfindungMit9Moeglichkeiten() {
		Terminfindung terminfindung = getBeispielTerminfindung();
		TerminfindungAntwort toSave = new TerminfindungAntwort();
		
		toSave.setAntworten(getBeispielAntwortenAlleJa(9));
		toSave.setKuerzel(BENUTZER1);
		when(terminRepo.findByLinkAndTermin(any(), any())).thenReturn(getBeispielTerminDBList(1).get(0));
		antwortService.abstimmen(toSave, terminfindung);
		
		System.out.println(toSave);
		Mockito.verify(antwortRepo, times(9)).save(any());
	}
	
	@Test
	public void loadByBenutzerUndLinkEinBenutzer4Moeglichkeiten() {
		int anzahl = 4;
		List<TerminfindungAntwortDB> terminfindungAntwortDBs = getBeispielAntwortDBList(anzahl, BENUTZER1);
		List<TerminfindungDB> terminfindungDBs = getBeispielTerminDBList(anzahl);
		
		when(antwortRepo.findByBenutzerAndTerminfindungLink(BENUTZER1, LINK))
				.thenReturn(terminfindungAntwortDBs);
		when(terminRepo.findByLink(LINK))
				.thenReturn(terminfindungDBs);
		TerminfindungAntwort ergebnis = antwortService.loadByBenutzerAndLink(BENUTZER1, LINK);
		TerminfindungAntwort erwartet = getBeispielTerminAntwort(anzahl, BENUTZER1);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadByLink4Benutzer() {
		int anzahlBenutzer = 4;
		List<TerminfindungAntwortDB> terminfindungAntwortDBs = getBeispieleAntwortDBList(anzahlBenutzer);
		List<TerminfindungDB> terminfindungDBs = getBeispielTerminDBList(4);
		when(antwortRepo.findAllByTerminfindungLink(LINK)).thenReturn(terminfindungAntwortDBs);
		when(terminRepo.findByLink(LINK))
				.thenReturn(terminfindungDBs);
		List<TerminfindungAntwort> ergebnis = antwortService.loadAllByLink(LINK);
		List<TerminfindungAntwort> erwartet = getBeispieleTerminAntwort(anzahlBenutzer);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	private List<TerminfindungAntwort> getBeispieleTerminAntwort(int anzahlBenutzer) {
		String name = "nameNr";
		List<TerminfindungAntwort> antworten = new ArrayList<>();
		for (int i = 0; i < anzahlBenutzer; i++) {
			antworten.add(getBeispielTerminAntwort(4, name + i));
		}
		return antworten;
	}
	
	private List<TerminfindungAntwortDB> getBeispieleAntwortDBList(int anzahlBenutzer) {
		String name = "nameNr";
		List<TerminfindungAntwortDB> antwortenDb = new ArrayList<>();
		for (int i = 0; i < anzahlBenutzer; i++) {
			antwortenDb.addAll(getBeispielAntwortDBList(4, name + i));
		}
		return antwortenDb;
	}
	
	private TerminfindungAntwort getBeispielTerminAntwort(int anzahl, String benutzer) {
		TerminfindungAntwort terminfindungAntwort = new TerminfindungAntwort();
		terminfindungAntwort.setLink(LINK);
		terminfindungAntwort.setAntworten(getBeispielAntwortenAlleJa(anzahl));
		terminfindungAntwort.setKuerzel(benutzer);
		terminfindungAntwort.setTeilgenommen(true);
		return terminfindungAntwort;
	}
	
	private HashMap<LocalDateTime, Antwort> getBeispielAntwortenAlleJa(int anzahl) {
		HashMap<LocalDateTime, Antwort> antworten = new HashMap<>();
		for (int j = 0; j < anzahl; j++) {
			antworten.put(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1).plusDays(j), Antwort.JA);
		}
		return antworten;
	}
	
	
	private Terminfindung getBeispielTerminfindung() {
		Terminfindung terminfindung = new Terminfindung();
		terminfindung.setLink(LINK);
		return terminfindung;
	}
	
	private List<TerminfindungAntwortDB> getBeispielAntwortDBList(int anzahl, String benutzer) {
		List<TerminfindungAntwortDB> antwortDBs = new ArrayList<>();
		HashMap<LocalDateTime, Antwort> antworten = getBeispielAntwortenAlleJa(anzahl);
		for (LocalDateTime termin : antworten.keySet()) {
			TerminfindungAntwortDB antwortDB = new TerminfindungAntwortDB();
			TerminfindungDB terminDB = new TerminfindungDB();
			terminDB.setLink(LINK);
			terminDB.setTermin(termin);
			antwortDB.setTerminfindung(terminDB);
			antwortDB.setBenutzer(benutzer);
			antwortDB.setAntwort(antworten.get(termin));
			antwortDBs.add(antwortDB);
		}
		
		return antwortDBs;
	}
	
	private List<TerminfindungDB> getBeispielTerminDBList(int anzahl) {
		List<TerminfindungDB> terminfindungAntwortDBS = new ArrayList<>();
		for (int i = 0; i < anzahl; i++) {
			TerminfindungDB terminfindungDB = new TerminfindungDB();
			terminfindungDB.setTermin(
					LocalDateTime.of(1, 1, 1, 1, 1, 1, 1).plusDays(i));
			terminfindungAntwortDBS.add(terminfindungDB);
		}
		return terminfindungAntwortDBS;
	}
}
