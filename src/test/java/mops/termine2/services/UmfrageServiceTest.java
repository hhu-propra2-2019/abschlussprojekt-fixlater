package mops.termine2.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Modus;
import mops.termine2.models.Umfrage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class UmfrageServiceTest {
	
	private transient UmfrageService service;
	
	private transient UmfrageRepository repository;
	
	@BeforeEach
	public void setUp() {
		repository = mock(UmfrageRepository.class);
		service = new UmfrageService(repository);
	}
	
	@Test
	public void saveEineUmfrageMitDreiVorschlaegen() {
		int anzahl = 3;
		Umfrage umfrage = erstelleBeispielUmfrage(anzahl);
		service.save(umfrage);
		Mockito.verify(repository, times(anzahl)).save(any());
	}
	
	@Test
	public void loadUmfrageByLinkMitDreiVorschlaegen() {
		int anzahl = 3;
		List<UmfrageDB> umfrageDBs = erstelleUmfrageDBListeGruppe(anzahl);
		when(repository.findByLink("BruderJakob")).thenReturn(umfrageDBs);
		Umfrage erwartet = erstelleBeispielUmfrage(anzahl);
		
		Umfrage ergebnis = service.loadByLink("BruderJakob");
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadUmfrageByLinkNichtExistent() {
		List<UmfrageDB> umfrageDBs = new ArrayList<UmfrageDB>();
		when(repository.findByLink("BruderJakob")).thenReturn(umfrageDBs);
		
		Umfrage ergebnis = service.loadByLink("BruderJakob");
		
		assertThat(ergebnis).isEqualTo(null);
	}
	
	@Test
	public void loadUmfrageByLinkNull() {
		when(repository.findByLink("BruderJakob")).thenReturn(null);
		
		Umfrage ergebnis = service.loadByLink("BruderJakob");
		
		assertThat(ergebnis).isEqualTo(null);
	}
	
	private List<UmfrageDB> erstelleUmfrageDBListeGruppe(int anzahl) {
		List<UmfrageDB> umfrageDBs = new ArrayList<UmfrageDB>();
		List<String> vorschlaege = erstelleVorschlaege(anzahl);
		for (String s : vorschlaege) {
			UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setAuswahlmoeglichkeit(s);
			umfrageDB.setBeschreibung("Tolle Beschreibung");
			umfrageDB.setErsteller("Me");
			umfrageDB.setFrist(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1));
			umfrageDB.setGruppe("FIXLATER");
			umfrageDB.setLink("BruderJakob");
			umfrageDB.setLoeschdatum(LocalDateTime.of(1, 3, 1, 1, 1, 1, 1));
			umfrageDB.setMaxAntwortAnzahl(13L);
			umfrageDB.setModus(Modus.GRUPPE);
			umfrageDB.setTitel("Toller Titel");
			umfrageDBs.add(umfrageDB);
		}
		return umfrageDBs;
		
	}
	
	private Umfrage erstelleBeispielUmfrage(int anzahl) {
		Umfrage umfrage = new Umfrage();
		umfrage.setBeschreibung("Tolle Beschreibung");
		umfrage.setErsteller("Me");
		umfrage.setFrist(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1));
		umfrage.setGruppe("FIXLATER");
		umfrage.setLink("BruderJakob");
		umfrage.setLoeschdatum(LocalDateTime.of(1, 3, 1, 1, 1, 1, 1));
		umfrage.setMaxAntwortAnzahl(13L);
		umfrage.setTitel("Toller Titel");
		List<String> vorschlaege = erstelleVorschlaege(anzahl);
		umfrage.setVorschlaege(vorschlaege);
		return umfrage;
		
	}
	
	private List<String> erstelleVorschlaege(int anzahl) {
		List<String> vorschlaege = new ArrayList<>();
		for (int i = 0; i < anzahl; i++) {
			vorschlaege.add("Vorschlag " + i);
		}
		return vorschlaege;
		
	}
	
}
