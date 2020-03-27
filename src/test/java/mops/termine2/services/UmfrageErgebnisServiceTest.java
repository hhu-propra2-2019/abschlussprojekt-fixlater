package mops.termine2.services;

import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Umfrage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UmfrageErgebnisServiceTest {
	
	private transient UmfrageAntwortRepository antwortRepo;
	
	private transient UmfrageErgebnisService ergebnisService;
	
	@BeforeEach
	private void setUp() {
		antwortRepo = mock(UmfrageAntwortRepository.class);
		ergebnisService = new UmfrageErgebnisService(antwortRepo);
	}
	
	@Test
	public void mehrheitJaAntwort1() {
		Umfrage t = new Umfrage();
		t.setVorschlaege(new ArrayList<>(Arrays.asList("M1", "M2")));
		when(antwortRepo.findAllByUmfrageLink(any())).thenReturn(beispielAntworten1());
		
		String expected = "M1";
		String ergebnis = ergebnisService.berechneErgebnisUmfrage(t);
		
		assertThat(ergebnis).isEqualTo(expected);
	}
	
	@Test
	public void mehrheitJaAntwort2() {
		Umfrage t = new Umfrage();
		t.setVorschlaege(new ArrayList<>(Arrays.asList("M1", "M2")));
		when(antwortRepo.findAllByUmfrageLink(any())).thenReturn(beispielAntworten2());
		
		String expected = "M2";
		String ergebnis = ergebnisService.berechneErgebnisUmfrage(t);
		
		assertThat(ergebnis).isEqualTo(expected);
	}
	
	
	private List<UmfrageAntwortDB> beispielAntworten1() {
		List<UmfrageAntwortDB> result = new ArrayList<>();
		String benutzer1 = "1";
		String benutzer2 = "2";
		String benutzer3 = "3";
		
		result.addAll(antwortForBenutzer(benutzer1, Antwort.JA, Antwort.NEIN));
		result.addAll(antwortForBenutzer(benutzer2, Antwort.JA, Antwort.JA));
		result.addAll(antwortForBenutzer(benutzer3, Antwort.JA, Antwort.JA));
		
		return result;
	}
	
	private List<UmfrageAntwortDB> beispielAntworten2() {
		List<UmfrageAntwortDB> result = new ArrayList<>();
		String benutzer1 = "1";
		String benutzer2 = "2";
		String benutzer3 = "3";
		
		result.addAll(antwortForBenutzer(benutzer1, Antwort.JA, Antwort.NEIN));
		result.addAll(antwortForBenutzer(benutzer2, Antwort.NEIN, Antwort.JA));
		result.addAll(antwortForBenutzer(benutzer3, Antwort.NEIN, Antwort.JA));
		
		return result;
	}
	
	private List<UmfrageAntwortDB> antwortForBenutzer(String name, Antwort antwort1, Antwort antwort2) {
		List<UmfrageAntwortDB> antwort = new ArrayList<>();
		
		UmfrageDB f1 = new UmfrageDB();
		f1.setAuswahlmoeglichkeit("M1");
		UmfrageDB f2 = new UmfrageDB();
		f2.setAuswahlmoeglichkeit("M2");
		
		UmfrageAntwortDB a1 = new UmfrageAntwortDB();
		a1.setBenutzer(name);
		a1.setUmfrage(f1);
		a1.setAntwort(antwort1);
		
		
		UmfrageAntwortDB a2 = new UmfrageAntwortDB();
		a2.setUmfrage(f2);
		a2.setAntwort(antwort2);
		a2.setBenutzer(name);
		antwort.add(a1);
		antwort.add(a2);
		return antwort;
	}
}

