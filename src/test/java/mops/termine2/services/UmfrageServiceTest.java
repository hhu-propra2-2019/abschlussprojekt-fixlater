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
	
	private static final String TITEL = "Toller Titel";
	
	private static final long MAXANTWORT = 13L;
	
	private static final LocalDateTime LOESCHDATUM = LocalDateTime.of(1, 3, 1, 1, 1, 1, 1);
	
	private static final String LINK = "BruderJakob";
	
	private static final String GRUPPE = "FIXLATER";
	
	private static final LocalDateTime FRIST = LocalDateTime.of(1, 1, 1, 1, 1, 1, 1);
	
	private static final String ERSTELLER = "Me";
	
	private static final String BESCHREIBUNG = "Tolle Beschreibung";
	
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
		when(repository.findByLink(LINK)).thenReturn(umfrageDBs);
		Umfrage erwartet = erstelleBeispielUmfrage(anzahl);
		
		Umfrage ergebnis = service.loadByLink(LINK);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadUmfrageByLinkNichtExistent() {
		List<UmfrageDB> umfrageDBs = new ArrayList<UmfrageDB>();
		when(repository.findByLink(LINK)).thenReturn(umfrageDBs);
		
		Umfrage ergebnis = service.loadByLink(LINK);
		
		assertThat(ergebnis).isEqualTo(null);
	}
	
	@Test
	public void loadUmfrageByLinkNull() {
		when(repository.findByLink(LINK)).thenReturn(null);
		
		Umfrage ergebnis = service.loadByLink(LINK);
		
		assertThat(ergebnis).isEqualTo(null);
	}
	
	@Test
	public void loadUmfragenByErstellerEineUmfrageDreiVorschlaege() {
		int anzahl = 3;
		List<UmfrageDB> umfrageDBs = erstelleUmfrageDBListeGruppe(anzahl);
		List<String> links = new ArrayList<String>();
		links.add(LINK);
		when(repository.findLinkByErsteller(ERSTELLER)).thenReturn(links);
		when(repository.findByLink(LINK)).thenReturn(umfrageDBs);
		Umfrage erwartet = erstelleBeispielUmfrage(anzahl);
		
		Umfrage ergebnis = service.loadByErsteller(ERSTELLER).get(0);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadUmfragenByGruppeEineUmfrageDreiVorschlaege() {
		int anzahl = 3;
		List<UmfrageDB> umfrageDBs = erstelleUmfrageDBListeGruppe(anzahl);
		List<String> links = new ArrayList<String>();
		links.add(LINK);
		when(repository.findLinkByGruppe(GRUPPE)).thenReturn(links);
		when(repository.findByLink(LINK)).thenReturn(umfrageDBs);
		Umfrage erwartet = erstelleBeispielUmfrage(anzahl);
		
		Umfrage ergebnis = service.loadByGruppe(GRUPPE).get(0);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	private List<UmfrageDB> erstelleUmfrageDBListeGruppe(int anzahl) {
		List<UmfrageDB> umfrageDBs = new ArrayList<UmfrageDB>();
		List<String> vorschlaege = erstelleVorschlaege(anzahl);
		for (String s : vorschlaege) {
			UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setAuswahlmoeglichkeit(s);
			umfrageDB.setBeschreibung(BESCHREIBUNG);
			umfrageDB.setErsteller(ERSTELLER);
			umfrageDB.setFrist(FRIST);
			umfrageDB.setGruppe(GRUPPE);
			umfrageDB.setLink(LINK);
			umfrageDB.setLoeschdatum(LOESCHDATUM);
			umfrageDB.setMaxAntwortAnzahl(13L);
			umfrageDB.setModus(Modus.GRUPPE);
			umfrageDB.setTitel(TITEL);
			umfrageDBs.add(umfrageDB);
		}
		return umfrageDBs;
		
	}
	
	private Umfrage erstelleBeispielUmfrage(int anzahl) {
		Umfrage umfrage = new Umfrage();
		umfrage.setBeschreibung(BESCHREIBUNG);
		umfrage.setErsteller(ERSTELLER);
		umfrage.setFrist(FRIST);
		umfrage.setGruppe(GRUPPE);
		umfrage.setLink(LINK);
		umfrage.setLoeschdatum(LOESCHDATUM);
		umfrage.setMaxAntwortAnzahl(MAXANTWORT);
		umfrage.setTitel(TITEL);
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
