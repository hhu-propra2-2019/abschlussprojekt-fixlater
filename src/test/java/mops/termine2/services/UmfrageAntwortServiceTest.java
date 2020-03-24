package mops.termine2.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Umfrage;
import mops.termine2.models.UmfrageAntwort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class UmfrageAntwortServiceTest {
	
	private static final String LINK = "BruderJakob";
	
	private static final String BENUTZER1 = "Me";
	
	private UmfrageAntwortRepository antwortRepo;
	
	private UmfrageRepository umfrageRepo;
	
	private UmfrageAntwortService antwortService;
	
	@BeforeEach
	public void setUp() {
		antwortRepo = mock(UmfrageAntwortRepository.class);
		umfrageRepo = mock(UmfrageRepository.class);
		
		antwortService = new UmfrageAntwortService(antwortRepo, umfrageRepo);
	}
	
	@Test
	public void saveAntwortFuerUmfrageMit3Moeglichkeiten() {
		int anzahl = 3;
		Umfrage umfrage = getBeispielUmfrage();
		UmfrageAntwort umfrageAntwort = new UmfrageAntwort();
		umfrageAntwort.setAntworten(getBeispielAntwortenAlleJa(anzahl));
		umfrageAntwort.setBenutzer(BENUTZER1);
		
		antwortService.abstimmen(umfrageAntwort, umfrage);
		
		Mockito.verify(antwortRepo, times(anzahl)).save(any());
		Mockito.verify(antwortRepo, times(1)).deleteAllByUmfrageLinkAndBenutzer(any(), any());
	}
	
	@Test
	public void saveAntwortFuerUmfrageMit17Moeglichkeiten() {
		int anzahl = 17;
		Umfrage umfrage = getBeispielUmfrage();
		UmfrageAntwort umfrageAntwort = new UmfrageAntwort();
		umfrageAntwort.setAntworten(getBeispielAntwortenAlleJa(anzahl));
		umfrageAntwort.setBenutzer(BENUTZER1);
		
		antwortService.abstimmen(umfrageAntwort, umfrage);
		
		Mockito.verify(antwortRepo, times(anzahl)).save(any());
		Mockito.verify(antwortRepo, times(1)).deleteAllByUmfrageLinkAndBenutzer(any(), any());
	}
	
	//@Test
	public void loadByBenutzerAndLinkEinBenutzer3Moeglichkeiten() {
		int anzahl = 3;
		List<UmfrageAntwortDB> umfrageAntwortDBs = getBeispielAntwortDBList(anzahl, BENUTZER1);
		when(antwortRepo.findByBenutzerAndUmfrageLink(BENUTZER1, LINK)).thenReturn(umfrageAntwortDBs);
		UmfrageAntwort ergebnis = antwortService.loadByBenutzerAndLink(BENUTZER1, LINK);
		UmfrageAntwort erwartet = getBeispielUmfrageAntwort(anzahl, BENUTZER1);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadAllByLink3Moeglichkeiten3Benutzer() {
		int anzahl = 3;
		int anzahlBenutzer = 3;
		List<UmfrageAntwortDB> umfrageAntwortDBs = getBeispieleAntwortDBList(anzahl, anzahlBenutzer);
		when(antwortRepo.findAllByUmfrageLink(LINK)).thenReturn(umfrageAntwortDBs);
		List<UmfrageAntwort> ergebnis = antwortService.loadAllByLink(LINK);
		List<UmfrageAntwort> erwartet = getBeispieleUmfrageAntwort(anzahl, anzahlBenutzer);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadAllByLink7Moeglichkeiten11Benutzer() {
		int anzahl = 7;
		int anzahlBenutzer = 11;
		List<UmfrageAntwortDB> umfrageAntwortDBs = getBeispieleAntwortDBList(anzahl, anzahlBenutzer);
		when(antwortRepo.findAllByUmfrageLink(LINK)).thenReturn(umfrageAntwortDBs);
		List<UmfrageAntwort> ergebnis = antwortService.loadAllByLink(LINK);
		List<UmfrageAntwort> erwartet = getBeispieleUmfrageAntwort(anzahl, anzahlBenutzer);
		
		assertThat(ergebnis).isEqualTo(erwartet);
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
	
	private UmfrageAntwort getBeispielUmfrageAntwort(int anzahl, String benutzer) {
		UmfrageAntwort umfrageAntwort = new UmfrageAntwort();
		umfrageAntwort.setAntworten(getBeispielAntwortenAlleJa(anzahl));
		umfrageAntwort.setBenutzer(benutzer);
		umfrageAntwort.setLink(LINK);
		umfrageAntwort.setTeilgenommen(true);
		return umfrageAntwort;
	}
	
	private List<UmfrageAntwort> getBeispieleUmfrageAntwort(int anzahl, int anzahlBenutzer) {
		List<UmfrageAntwort> umfrageAntworten = new ArrayList<>();
		String benutzername = "Benutzer";
		for (int i = 0; i < anzahlBenutzer; i++) {
			umfrageAntworten.add(getBeispielUmfrageAntwort(anzahl, benutzername + i));
		}
		return umfrageAntworten;
	}
	
	private List<UmfrageAntwortDB> getBeispielAntwortDBList(int anzahl, String benutzer) {
		List<UmfrageAntwortDB> umfrageAntwortDBs = new ArrayList<>();
		HashMap<String, Antwort> antworten = getBeispielAntwortenAlleJa(anzahl);
		for (String vorschlag : antworten.keySet()) {
			UmfrageAntwortDB umfrageAntwortDB = new UmfrageAntwortDB();
			UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setLink(LINK);
			umfrageDB.setAuswahlmoeglichkeit(vorschlag);
			umfrageAntwortDB.setAntwort(antworten.get(vorschlag));
			umfrageAntwortDB.setBenutzer(benutzer);
			umfrageAntwortDB.setUmfrage(umfrageDB);
			
			umfrageAntwortDBs.add(umfrageAntwortDB);
		}
		return umfrageAntwortDBs;
	}
	
	private List<UmfrageAntwortDB> getBeispieleAntwortDBList(int anzahl, int anzahlBenutzer) {
		List<UmfrageAntwortDB> umfrageAntwortDBs = new ArrayList<>();
		String benutzername = "Benutzer";
		for (int i = 0; i < anzahlBenutzer; i++) {
			umfrageAntwortDBs.addAll(getBeispielAntwortDBList(anzahl, benutzername + i));
		}
		return umfrageAntwortDBs;
	}
	
}
