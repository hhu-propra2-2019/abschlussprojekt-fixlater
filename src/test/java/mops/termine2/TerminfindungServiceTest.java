package mops.termine2;

import mops.termine2.database.TerminfindungRepository;
import mops.termine2.models.Terminfindung;
import mops.termine2.services.TerminfindungService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class TerminfindungServiceTest {
	
	TerminfindungService service;
	
	TerminfindungRepository repository;
	
	@BeforeEach
	public void setup() {
		repository = mock(TerminfindungRepository.class);
		this.service = new TerminfindungService(repository);
	}
	
	@Test
	public void saveSingleTerminfndungMit5Vorschlaegen() {
		int terminAnzahl = 5;
		Terminfindung termine = erstelleBeispielTermin(terminAnzahl);
		service.save(termine);
		Mockito.verify(repository, times(terminAnzahl)).save(any());
	}
	
	@Test
	public void saveSingleTerminfndungMit10Vorschlaegen() {
		int terminAnzahl = 10;
		Terminfindung termine = erstelleBeispielTermin(terminAnzahl);
		service.save(termine);
		Mockito.verify(repository, times(terminAnzahl)).save(any());
	}
	
	@Test
	public void saveSingleTerminfndungMit1000Vorschlaegen() {
		int terminAnzahl = 1000;
		Terminfindung termine = erstelleBeispielTermin(terminAnzahl);
		service.save(termine);
		Mockito.verify(repository, times(terminAnzahl)).save(any());
	}
	
	private Terminfindung erstelleBeispielTermin(int anzahlTermine) {
		Terminfindung termine = new Terminfindung();
		termine.setBeschreibung("Beschreibung");
		termine.setErsteller("javan108");
		
		LocalDateTime frist = LocalDateTime.now().plusWeeks(2);
		termine.setLoeschdatum(frist.plusMonths(2));
		termine.setFrist(frist);
		termine.setGruppe("fixlater");
		termine.setLink("safiuzewcewzfew7b63r");
		termine.setOrt("25.13.U1.24");
		termine.setTitel("WirBasteln");
		List<LocalDateTime> terminVorschlaege = erstelleVorschlaege(anzahlTermine);
		termine.setVorschlaege(terminVorschlaege);
		return termine;
	}
	
	private List<LocalDateTime> erstelleVorschlaege(int anzahlTermine) {
		List<LocalDateTime> terminVorschleage = new ArrayList<>();
		for (int i = 0; i < anzahlTermine; i++) {
			LocalDateTime termin = LocalDateTime.now().plusDays(i);
			terminVorschleage.add(termin);
		}
		return terminVorschleage;
	}
	
}
