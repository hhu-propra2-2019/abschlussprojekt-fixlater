package mops.termine2.services;

import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Umfrage;
import mops.termine2.models.UmfrageAntwort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class UmfrageAntwortServiceTest {
	
	public static final String LINK = "IchBinKeinMannFuerEineNacht,IchBleibeHoechstensEinZweiStunden";
	
	public static final String BENUTZER1 = "Julia";
	
	private transient UmfrageAntwortRepository antwortRepo;
	
	private transient UmfrageAntwortService antwortService;
	
	private transient UmfrageRepository umfrageRepo;
	
	@BeforeEach
	private void setUp() {
		antwortRepo = mock(UmfrageAntwortRepository.class);
		umfrageRepo = mock(UmfrageRepository.class);
		antwortService = new UmfrageAntwortService(antwortRepo, umfrageRepo);
	}
	
	@Test
	public void saveAntwortFuerUmfrageMit4Moeglichkeiten() {
		Umfrage umfrage = getBeispielUmfrage(4);
		UmfrageAntwort toSave = new UmfrageAntwort();
		
		toSave.setAntworten(getBeispielAntwortenAlleJa(4));
		toSave.setBenutzer(BENUTZER1);
		when(umfrageRepo.findByLinkAndAuswahlmoeglichkeit(any(), any()))
			.thenReturn(getBeispielUmfrageDBList(1).get(0));
		antwortService.abstimmen(toSave, umfrage);
		
		Mockito.verify(antwortRepo, times(4)).save(any());
		
	}
	
	@Test
	public void saveAntwortFuerUmfrageMit9Moeglichkeiten() {
		Umfrage umfrage = getBeispielUmfrage(9);
		UmfrageAntwort toSave = new UmfrageAntwort();
		
		toSave.setAntworten(getBeispielAntwortenAlleJa(9));
		toSave.setBenutzer(BENUTZER1);
		when(umfrageRepo.findByLinkAndAuswahlmoeglichkeit(any(), any()))
			.thenReturn(getBeispielUmfrageDBList(1).get(0));
		antwortService.abstimmen(toSave, umfrage);
		
		Mockito.verify(antwortRepo, times(9)).save(any());
		
	}
	
	@Test
	public void loadByBenutzerUndLinkEinBenutzer4Moeglichkeiten() {
		int anzahl = 4;
		List<UmfrageAntwortDB> umfrageAntwortDBs = getBeispielAntwortDBList(anzahl, BENUTZER1);
		List<UmfrageDB> umfrageDBs = getBeispielUmfrageDBList(anzahl);
		
		when(antwortRepo.findByBenutzerAndUmfrageLink(BENUTZER1, LINK))
			.thenReturn(umfrageAntwortDBs);
		when(umfrageRepo.findByLink(LINK))
			.thenReturn(umfrageDBs);
		UmfrageAntwort ergebnis = antwortService.loadByBenutzerUndLink(BENUTZER1, LINK);
		UmfrageAntwort erwartet = getBeispielUmfrageAntwort(anzahl, BENUTZER1);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadByLink4Benutzer() {
		int anzahlBenutzer = 4;
		List<UmfrageAntwortDB> beispielAntwort = getBeispieleAntwortDBList(anzahlBenutzer);
		List<UmfrageDB> umfrageDBs = getBeispielUmfrageDBList(4);
		when(antwortRepo.findAllByUmfrageLink(LINK)).thenReturn(beispielAntwort);
		when(umfrageRepo.findByLink(LINK))
			.thenReturn(umfrageDBs);
		List<UmfrageAntwort> ergebnis = antwortService.loadAllByLink(LINK);
		List<UmfrageAntwort> erwartet = getBeispieleUmfrageAntwort(anzahlBenutzer);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	private List<UmfrageAntwort> getBeispieleUmfrageAntwort(int anzahlBenutzer) {
		String name = "nameNr";
		List<UmfrageAntwort> antworten = new ArrayList<>();
		for (int i = 0; i < anzahlBenutzer; i++) {
			antworten.add(getBeispielUmfrageAntwort(4, name + i));
		}
		return antworten;
	}
	
	private List<UmfrageAntwortDB> getBeispieleAntwortDBList(int anzahlBenutzer) {
		String name = "nameNr";
		List<UmfrageAntwortDB> antwortenDb = new ArrayList<>();
		for (int i = 0; i < anzahlBenutzer; i++) {
			antwortenDb.addAll(getBeispielAntwortDBList(4, name + i));
		}
		return antwortenDb;
	}
	
	private UmfrageAntwort getBeispielUmfrageAntwort(int anzahl, String benutzer) {
		UmfrageAntwort umfrageAntwort = new UmfrageAntwort();
		umfrageAntwort.setLink(LINK);
		umfrageAntwort.setAntworten(getBeispielAntwortenAlleJa(anzahl));
		umfrageAntwort.setBenutzer(benutzer);
		return umfrageAntwort;
	}
	
	private LinkedHashMap<String, Antwort> getBeispielAntwortenAlleJa(int anzahl) {
		LinkedHashMap<String, Antwort> antworten = new LinkedHashMap<>();
		for (int j = 0; j < anzahl; j++) {
			String antwort = Integer.toString(j);
			antworten.put(antwort, Antwort.JA);
		}
		return antworten;
	}
	
	private Umfrage getBeispielUmfrage(int anzahl) {
		Umfrage umfrage = new Umfrage();
		umfrage.setLink(LINK);
		umfrage.setVorschlaege(new ArrayList<>());
		for (int j = 0; j < anzahl; j++) {
			umfrage.getVorschlaege().add(Integer.toString(j));
		}
		return umfrage;
	}
	
	private List<UmfrageAntwortDB> getBeispielAntwortDBList(int anzahl, String benutzer) {
		List<UmfrageAntwortDB> antwortDBs = new ArrayList<>();
		LinkedHashMap<String, Antwort> antworten = getBeispielAntwortenAlleJa(anzahl);
		for (String vorschlag : antworten.keySet()) {
			UmfrageAntwortDB antwortDB = new UmfrageAntwortDB();
			UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setLink(LINK);
			umfrageDB.setAuswahlmoeglichkeit(vorschlag);
			antwortDB.setUmfrage(umfrageDB);
			antwortDB.setBenutzer(benutzer);
			antwortDB.setAntwort(antworten.get(vorschlag));
			antwortDBs.add(antwortDB);
		}
		
		return antwortDBs;
	}
	
	private List<UmfrageDB> getBeispielUmfrageDBList(int anzahl) {
		List<UmfrageDB> umfrageAntwortDBS = new ArrayList<>();
		for (int i = 0; i < anzahl; i++) {
			UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setLink(LINK);
			umfrageDB.setAuswahlmoeglichkeit(Integer.toString(i));
			umfrageAntwortDBS.add(umfrageDB);
		}
		return umfrageAntwortDBS;
	}
}
