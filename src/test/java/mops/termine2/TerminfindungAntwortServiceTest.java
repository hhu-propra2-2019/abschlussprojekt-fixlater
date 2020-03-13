package mops.termine2;

import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;
import mops.termine2.services.TerminAntwortService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class TerminfindungAntwortServiceTest {
	
	public static final String LINK = "IchBinKeinMannFuerEineNacht,IchBleibeHoechstensEinZweiStunden";
	
	public static final String BENUTZER1 = "Julia";
	
	public static final String BENUTZER2 = "Kathi";
	
	public static final String BENUTZER3 = "Ingid";
	
	private transient TerminfindungAntwortRepository repo;
	
	private transient TerminAntwortService antwortService;
	
	@BeforeEach
	private void setUp() {
		repo = mock(TerminfindungAntwortRepository.class);
		antwortService = new TerminAntwortService(repo);
	}
	
	@Test
	public void saveAntwortFuerTerminfindungMit4Moeglichkeiten() {
		Terminfindung terminfindung = getBeispielTerinfindung();
		TerminfindungAntwort toSave = new TerminfindungAntwort();
		
		toSave.setAntworten(getBeispielAntwortenAlleJa(4));
		toSave.setKuerzel(BENUTZER1);
		
		antwortService.abstimmen(toSave, terminfindung);
		
		Mockito.verify(repo, times(4)).save(any());
		Mockito.verify(repo, times(1)).deleteAllByTerminfindungLinkAAndBenutzer(any(), any());
	}
	
	@Test
	public void saveAntwortFuerTerminfindungMit9Moeglichkeiten() {
		Terminfindung terminfindung = getBeispielTerinfindung();
		TerminfindungAntwort toSave = new TerminfindungAntwort();
		
		toSave.setAntworten(getBeispielAntwortenAlleJa(9));
		toSave.setKuerzel(BENUTZER1);
		
		antwortService.abstimmen(toSave, terminfindung);
		
		Mockito.verify(repo, times(9)).save(any());
		Mockito.verify(repo, times(1)).deleteAllByTerminfindungLinkAAndBenutzer(any(), any());
	}
	
	private HashMap<LocalDateTime, Antwort> getBeispielAntwortenAlleJa(int anzahl) {
		HashMap<LocalDateTime, Antwort> antworten = new HashMap<>();
		for (int j = 0; j < anzahl; j++) {
			antworten.put(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1).plusDays(j), Antwort.JA);
		}
		return antworten;
	}
	
	
	private Terminfindung getBeispielTerinfindung() {
		Terminfindung terminfindung = new Terminfindung();
		terminfindung.setLink(LINK);
		return terminfindung;
	}
}
