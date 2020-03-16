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
	
	private static final String[] TITEL = {"Toller Titel", "Besserer Titel", "Super Titel"};
	
	private static final long MAXANTWORT = 13L;
	
	private static final LocalDateTime LOESCHDATUM = LocalDateTime.of(1, 3, 1, 1, 1, 1, 1);
	
	private static final String[] LINK = {"BruderJakob", "AlleMeineEntchen", "BieneMaya"};
	
	private static final String[] GRUPPE = {"FIXLATER", "TollEinAndererMachts", "Proprapri"};
	
	private static final LocalDateTime FRIST = LocalDateTime.of(1, 1, 1, 1, 1, 1, 1);
	
	private static final String[] ERSTELLER = {"Me", "You", "He"};
	
	private static final String[] BESCHREIBUNG = {"Tolle Beschreibung",
		"Bessere Beschreibung",
		"Super Beschreibung"};
	
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
		Umfrage umfrage = erstelleBeispielUmfrage(anzahl, 0, 0, 0, 0, 0);
		service.save(umfrage);
		Mockito.verify(repository, times(anzahl)).save(any());
	}
	
	@Test
	public void loadUmfrageByLinkMitDreiVorschlaegen() {
		int anzahl = 3;
		List<UmfrageDB> umfrageDBs = erstelleUmfrageDBListeGruppe(anzahl, 0, 0, 0, 0, 0);
		when(repository.findByLink(LINK[0])).thenReturn(umfrageDBs);
		Umfrage erwartet = erstelleBeispielUmfrage(anzahl, 0, 0, 0, 0, 0);
		
		Umfrage ergebnis = service.loadByLink(LINK[0]);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadUmfrageByLinkNichtExistent() {
		List<UmfrageDB> umfrageDBs = new ArrayList<UmfrageDB>();
		when(repository.findByLink(LINK[0])).thenReturn(umfrageDBs);
		
		Umfrage ergebnis = service.loadByLink(LINK[0]);
		
		assertThat(ergebnis).isEqualTo(null);
	}
	
	@Test
	public void loadUmfrageByLinkNull() {
		when(repository.findByLink(LINK[0])).thenReturn(null);
		
		Umfrage ergebnis = service.loadByLink(LINK[0]);
		
		assertThat(ergebnis).isEqualTo(null);
	}
	
	@Test
	public void loadUmfragenByErstellerEineUmfrageKeineVorschlaege() {
		int anzahl = 3;
		List<UmfrageDB> umfrageDBs = erstelleUmfrageDBListeGruppe(anzahl, 0, 0, 0, 0, 0);
		when(repository.findByErsteller(ERSTELLER[0])).thenReturn(umfrageDBs);
		Umfrage erwartet = erstelleBeispielUmfrage(anzahl, 0, 0, 0, 0, 0);
		erwartet.setVorschlaege(new ArrayList<String>());
		
		Umfrage ergebnis = service.loadByErsteller(ERSTELLER[0]).get(0);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadUmfragenByErstellerZweiUmfragenEinErsteller() {
		List<UmfrageDB> umfrageDBs1 = erstelleUmfrageDBListeGruppe(3, 0, 0, 0, 0, 0);
		List<UmfrageDB> umfrageDBs2 = erstelleUmfrageDBListeGruppe(3, 1, 0, 1, 1, 1);
		List<UmfrageDB> umfrageDBs = new ArrayList<UmfrageDB>(umfrageDBs1);
		for (UmfrageDB db : umfrageDBs2) {
			umfrageDBs.add(db);
		}
		when(repository.findByErsteller(ERSTELLER[0])).thenReturn(umfrageDBs);
		Umfrage erwartet1 = erstelleBeispielUmfrage(3, 0, 0, 0, 0, 0);
		erwartet1.setVorschlaege(new ArrayList<String>());
		Umfrage erwartet2 = erstelleBeispielUmfrage(3, 1, 0, 1, 1, 1);
		erwartet2.setVorschlaege(new ArrayList<String>());
		
		Umfrage ergebnis1 = service.loadByErsteller(ERSTELLER[0]).get(0);
		Umfrage ergebnis2 = service.loadByErsteller(ERSTELLER[0]).get(1);
		
		assertThat(erwartet1).isEqualTo(ergebnis1);
		assertThat(erwartet2).isEqualTo(ergebnis2);
	}
	
	@Test
	public void loadUmfragenByGruppeEineUmfrageKeineVorschlaege() {
		int anzahl = 3;
		List<UmfrageDB> umfrageDBs = erstelleUmfrageDBListeGruppe(anzahl, 0, 0, 0, 0, 0);
		when(repository.findByGruppe(GRUPPE[0])).thenReturn(umfrageDBs);
		Umfrage erwartet = erstelleBeispielUmfrage(anzahl, 0, 0, 0, 0, 0);
		erwartet.setVorschlaege(new ArrayList<String>());
		
		Umfrage ergebnis = service.loadByGruppe(GRUPPE[0]).get(0);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadUmfragenByGruppeZweiUmfragenEineGruppe() {
		List<UmfrageDB> umfrageDBs1 = erstelleUmfrageDBListeGruppe(3, 0, 0, 0, 0, 0);
		List<UmfrageDB> umfrageDBs2 = erstelleUmfrageDBListeGruppe(3, 1, 1, 0, 1, 1);
		List<UmfrageDB> umfrageDBs = new ArrayList<UmfrageDB>(umfrageDBs1);
		for (UmfrageDB db : umfrageDBs2) {
			umfrageDBs.add(db);
		}
		when(repository.findByGruppe(GRUPPE[0])).thenReturn(umfrageDBs);
		Umfrage erwartet1 = erstelleBeispielUmfrage(3, 0, 0, 0, 0, 0);
		erwartet1.setVorschlaege(new ArrayList<String>());
		Umfrage erwartet2 = erstelleBeispielUmfrage(3, 1, 1, 0, 1, 1);
		erwartet2.setVorschlaege(new ArrayList<String>());
		
		Umfrage ergebnis1 = service.loadByGruppe(GRUPPE[0]).get(0);
		Umfrage ergebnis2 = service.loadByGruppe(GRUPPE[0]).get(1);
		
		assertThat(erwartet1).isEqualTo(ergebnis1);
		assertThat(erwartet2).isEqualTo(ergebnis2);
	}
	
	private List<UmfrageDB> erstelleUmfrageDBListeGruppe(int anzahl, int bIndex,
		int eIndex, int gIndex, int lIndex, int tIndex) {
		List<UmfrageDB> umfrageDBs = new ArrayList<UmfrageDB>();
		List<String> vorschlaege = erstelleVorschlaege(anzahl);
		for (String s : vorschlaege) {
			UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setAuswahlmoeglichkeit(s);
			umfrageDB.setBeschreibung(BESCHREIBUNG[bIndex]);
			umfrageDB.setErsteller(ERSTELLER[eIndex]);
			umfrageDB.setFrist(FRIST);
			umfrageDB.setGruppe(GRUPPE[gIndex]);
			umfrageDB.setLink(LINK[lIndex]);
			umfrageDB.setLoeschdatum(LOESCHDATUM);
			umfrageDB.setMaxAntwortAnzahl(13L);
			umfrageDB.setModus(Modus.GRUPPE);
			umfrageDB.setTitel(TITEL[tIndex]);
			umfrageDBs.add(umfrageDB);
		}
		return umfrageDBs;
		
	}
	
	private Umfrage erstelleBeispielUmfrage(int anzahl, int bIndex, int eIndex,
		int gIndex, int lIndex, int tIndex) {
		Umfrage umfrage = new Umfrage();
		umfrage.setBeschreibung(BESCHREIBUNG[bIndex]);
		umfrage.setErsteller(ERSTELLER[eIndex]);
		umfrage.setFrist(FRIST);
		umfrage.setGruppe(GRUPPE[gIndex]);
		umfrage.setLink(LINK[lIndex]);
		umfrage.setLoeschdatum(LOESCHDATUM);
		umfrage.setMaxAntwortAnzahl(MAXANTWORT);
		umfrage.setTitel(TITEL[tIndex]);
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