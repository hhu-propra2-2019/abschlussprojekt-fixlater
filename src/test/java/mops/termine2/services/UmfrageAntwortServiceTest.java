package mops.termine2.services;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Umfrage;
import mops.termine2.models.UmfrageAntwort;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class UmfrageAntwortServiceTest {
	
	private static final String LINK = "BruderJakob";
	
	private static final String BENUTZER1 = "Me";
	
	private UmfrageAntwortRepository repo;
	
	private UmfrageAntwortService antwortService;
	
	@BeforeEach
	public void setUp() {
		repo = mock(UmfrageAntwortRepository.class);
		antwortService = new UmfrageAntwortService(repo);
	}
	
	@Test
	public void saveAntwortFuerUmfrageMit3Moeglichkeiten() {
		int anzahl = 3;
		Umfrage umfrage = getBeispielUmfrage();
		UmfrageAntwort umfrageAntwort = new UmfrageAntwort();
		umfrageAntwort.setAntworten(getBeispielAntwortenAlleJa(anzahl));
		umfrageAntwort.setBenutzer(BENUTZER1);
		
		antwortService.abstimmen(umfrageAntwort, umfrage);
		
		Mockito.verify(repo, times(anzahl)).save(any());
		Mockito.verify(repo, times(1)).deleteAllByUmfrageLinkAndBenutzer(any(), any());
	}
	
	@Test
	public void saveAntwortFuerUmfrageMit17Moeglichkeiten() {
		int anzahl = 17;
		Umfrage umfrage = getBeispielUmfrage();
		UmfrageAntwort umfrageAntwort = new UmfrageAntwort();
		umfrageAntwort.setAntworten(getBeispielAntwortenAlleJa(anzahl));
		umfrageAntwort.setBenutzer(BENUTZER1);
		
		antwortService.abstimmen(umfrageAntwort, umfrage);
		
		Mockito.verify(repo, times(anzahl)).save(any());
		Mockito.verify(repo, times(1)).deleteAllByUmfrageLinkAndBenutzer(any(), any());
	}
	
	private Umfrage getBeispielUmfrage() {
		Umfrage umfrage = new Umfrage();
		umfrage.setLink(LINK);
		return umfrage;
	}
	
	private HashMap<String, Antwort> getBeispielAntwortenAlleJa(int anzahl) {
		HashMap<String, Antwort> antworten = new HashMap<>();
		for (int i = 0; i < anzahl; i++) {
			antworten.put("Vorschlag " + i, Antwort.JA);
		}
		return antworten;
	}
	
}
