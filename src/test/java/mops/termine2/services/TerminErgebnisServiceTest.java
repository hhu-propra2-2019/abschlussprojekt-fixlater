package mops.termine2.services;

import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Terminfindung;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TerminErgebnisServiceTest {
	
	private transient TerminfindungAntwortRepository antwortRepo;
	
	private transient TerminErgebnisService ergebnisService;
	
	@BeforeEach
	private void setUp() {
		antwortRepo = mock(TerminfindungAntwortRepository.class);
		ergebnisService = new TerminErgebnisService(antwortRepo);
	}
	
	@Test
	public void mehrheitAntwort1() {
		Terminfindung t = new Terminfindung();
		t.setVorschlaege(new ArrayList<>(Arrays.asList(
			LocalDateTime.of(1, 1, 1, 1, 1),
			LocalDateTime.of(1, 1, 1, 1, 2)
		)));
		when(antwortRepo.findAllByTerminfindungLink(any())).thenReturn(beispielAntworten1());
		
		LocalDateTime expected = LocalDateTime.of(1, 1, 1, 1, 1);
		LocalDateTime ergebnis = ergebnisService.berechneErgebnisTerminfindung(t);
		
		assertThat(ergebnis).isEqualTo(expected);
	}
	
	@Test
	public void mehrheitAntwort2() {
		Terminfindung t = new Terminfindung();
		t.setVorschlaege(new ArrayList<>(Arrays.asList(
			LocalDateTime.of(1, 1, 1, 1, 1),
			LocalDateTime.of(1, 1, 1, 1, 2)
		)));
		when(antwortRepo.findAllByTerminfindungLink(any())).thenReturn(beispielAntworten2());
		
		LocalDateTime expected = LocalDateTime.of(1, 1, 1, 1, 2);
		LocalDateTime ergebnis = ergebnisService.berechneErgebnisTerminfindung(t);
		
		assertThat(ergebnis).isEqualTo(expected);
	}
	
	@Test
	public void mehrheitDurchVielleicht() {
		Terminfindung t = new Terminfindung();
		t.setVorschlaege(new ArrayList<>(Arrays.asList(
			LocalDateTime.of(1, 1, 1, 1, 1),
			LocalDateTime.of(1, 1, 1, 1, 2)
		)));
		when(antwortRepo.findAllByTerminfindungLink(any())).thenReturn(beispielAntworten3());
		
		LocalDateTime expected = LocalDateTime.of(1, 1, 1, 1, 1);
		LocalDateTime ergebnis = ergebnisService.berechneErgebnisTerminfindung(t);
		
		assertThat(ergebnis).isEqualTo(expected);
	}
	
	private List<TerminfindungAntwortDB> beispielAntworten1() {
		List<TerminfindungAntwortDB> result = new ArrayList<>();
		String benutzer1 = "1";
		String benutzer2 = "2";
		String benutzer3 = "3";
		
		result.addAll(antwortForBenutzer(benutzer1, Antwort.JA, Antwort.NEIN));
		result.addAll(antwortForBenutzer(benutzer2, Antwort.JA, Antwort.JA));
		result.addAll(antwortForBenutzer(benutzer3, Antwort.JA, Antwort.JA));
		
		return result;
	}
	
	private List<TerminfindungAntwortDB> beispielAntworten2() {
		List<TerminfindungAntwortDB> result = new ArrayList<>();
		String benutzer1 = "1";
		String benutzer2 = "2";
		String benutzer3 = "3";
		
		result.addAll(antwortForBenutzer(benutzer1, Antwort.JA, Antwort.NEIN));
		result.addAll(antwortForBenutzer(benutzer2, Antwort.VIELLEICHT, Antwort.JA));
		result.addAll(antwortForBenutzer(benutzer3, Antwort.VIELLEICHT, Antwort.JA));
		
		return result;
	}
	
	private List<TerminfindungAntwortDB> beispielAntworten3() {
		List<TerminfindungAntwortDB> result = new ArrayList<>();
		String benutzer1 = "1";
		String benutzer2 = "2";
		String benutzer3 = "3";
		String benutzer4 = "4";
		
		result.addAll(antwortForBenutzer(benutzer1, Antwort.JA, Antwort.NEIN));
		result.addAll(antwortForBenutzer(benutzer2, Antwort.JA, Antwort.JA));
		result.addAll(antwortForBenutzer(benutzer3, Antwort.VIELLEICHT, Antwort.VIELLEICHT));
		result.addAll(antwortForBenutzer(benutzer4, Antwort.VIELLEICHT, Antwort.JA));
		
		return result;
	}
	
	private List<TerminfindungAntwortDB> antwortForBenutzer(String name, Antwort antwort1, Antwort antwort2) {
		List<TerminfindungAntwortDB> antwort = new ArrayList<>();
		
		TerminfindungDB f1 = new TerminfindungDB();
		f1.setTermin(LocalDateTime.of(1, 1, 1, 1, 1));
		TerminfindungDB f2 = new TerminfindungDB();
		f2.setTermin(LocalDateTime.of(1, 1, 1, 1, 2));
		
		TerminfindungAntwortDB a1 = new TerminfindungAntwortDB();
		a1.setBenutzer(name);
		a1.setTerminfindung(f1);
		a1.setAntwort(antwort1);
		
		
		TerminfindungAntwortDB a2 = new TerminfindungAntwortDB();
		a2.setTerminfindung(f2);
		a2.setAntwort(antwort2);
		a2.setBenutzer(name);
		antwort.add(a1);
		antwort.add(a2);
		return antwort;
	}
}
