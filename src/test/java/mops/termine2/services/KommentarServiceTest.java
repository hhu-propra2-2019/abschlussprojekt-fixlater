package mops.termine2.services;

import mops.termine2.database.KommentarRepository;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.KommentarDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Modus;
import mops.termine2.models.Kommentar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class KommentarServiceTest {
	
	private transient KommentarService service;
	
	private transient KommentarRepository kommentarRepository;
	
	private transient TerminfindungRepository terminfindungRepository;
	
	private transient UmfrageRepository umfrageRepository;
	
	private transient String beschreibung = "Beschreibung";
	
	private transient LocalDateTime ergebnisTermin = LocalDateTime.of(1, 1, 2, 1, 1, 1, 1);
	
	private transient String ergebnisUmfrage = "Vorschlag";
	
	private transient String ersteller = "studentin1";
	
	private transient LocalDateTime erstellungsdatum = LocalDateTime.now();
	
	private transient LocalDateTime frist = LocalDateTime.of(1, 1, 1, 1, 1, 1, 1);
	
	private transient Long gruppeId = 1L;
	
	private transient String inhalt = "kommentar";
	
	private transient String link = "link";
	
	private transient LocalDateTime loeschdatum = LocalDateTime.of(1, 1, 3, 1, 1, 1, 1);
	
	private transient Long maxAntwortAnzahl = 1L;
	
	private transient String ort = "Ort";
	
	private transient String pseudonym = "pseudonym";
	
	private transient String titel = "Titel";
	
	@BeforeEach
	public void setUp() {
		kommentarRepository = mock(KommentarRepository.class);
		terminfindungRepository = mock(TerminfindungRepository.class);
		umfrageRepository = mock(UmfrageRepository.class);
		service = new KommentarService(kommentarRepository, terminfindungRepository, umfrageRepository);
	}
	
	@Test
	public void saveEinenKommmentar() {
		int anzahl = 1;
		List<Kommentar> kommentar = erstelleKommentarListe(anzahl);
		
		service.save(kommentar.get(0));
		
		Mockito.verify(kommentarRepository, times(anzahl)).save(any());
	}
	
	@Test
	public void saveDreiKommentare() {
		int anzahl = 3;
		List<Kommentar> kommentare = erstelleKommentarListe(anzahl);
		
		IntStream.range(1, anzahl + 1).forEach(kommentarNummer -> {
			service.save(kommentare.get(kommentarNummer - 1));
		});
		
		Mockito.verify(kommentarRepository, times(anzahl)).save(any());
		
	}
	
	@Test
	public void loadByLinkEinenKommentar() {
		int anzahl = 1;
		List<KommentarDB> kommentarDBs = erstelleKommentarDBListe(anzahl);
		when(kommentarRepository.findByLinkOrderByErstellungsdatumAsc(link)).thenReturn(kommentarDBs);
		List<Kommentar> kommentareErwartet = erstelleKommentarListe(anzahl);
		
		List<Kommentar> kommentare = service.loadByLink(link);
		
		assertThat(kommentare).isEqualTo(kommentareErwartet);
	}
	
	@Test
	public void loadByLinkDreiKommentare() {
		int anzahl = 3;
		List<KommentarDB> kommentarDBs = erstelleKommentarDBListe(anzahl);
		when(kommentarRepository.findByLinkOrderByErstellungsdatumAsc(link)).thenReturn(kommentarDBs);
		List<Kommentar> kommentareErwartet = erstelleKommentarListe(anzahl);
		
		List<Kommentar> kommentare = service.loadByLink(link);
		
		assertThat(kommentare).isEqualTo(kommentareErwartet);
	}
	
	@Test
	public void loescheAbgelaufeneKommentareFuerEinenTermin() {
		int anzahl = 1;
		List<TerminfindungDB> terminfindungDBs = erstelleTerminfindungDBListe(anzahl);
		when(terminfindungRepository.findByLoeschdatumBefore(any())).thenReturn(terminfindungDBs);
		String linkErwartet = link + 1;
		
		service.loescheAbgelaufeneKommentareFuerTermine();
		
		Mockito.verify(kommentarRepository, times(anzahl)).deleteByLink(linkErwartet);
	}
	
	@Test
	public void loescheAbgelaufeneKommentareFuerDreiTermine() {
		int anzahl = 3;
		List<TerminfindungDB> terminfindungDBs = erstelleTerminfindungDBListe(anzahl);
		when(terminfindungRepository.findByLoeschdatumBefore(any())).thenReturn(terminfindungDBs);
		String linkErwartet1 = link + 1;
		String linkErwartet2 = link + 2;
		String linkErwartet3 = link + 3;
		
		service.loescheAbgelaufeneKommentareFuerTermine();
		
		Mockito.verify(kommentarRepository, times(1)).deleteByLink(linkErwartet1);
		Mockito.verify(kommentarRepository, times(1)).deleteByLink(linkErwartet2);
		Mockito.verify(kommentarRepository, times(1)).deleteByLink(linkErwartet3);
	}
	
	@Test
	public void loescheAbgelaufeneKommentareFuerEineUmfrage() {
		int anzahl = 1;
		List<UmfrageDB> umfrageDBs = erstelleUmfrageDBListe(anzahl);
		when(umfrageRepository.findByLoeschdatumBefore(any())).thenReturn(umfrageDBs);
		String linkErwartet = link + 1;
		
		service.loescheAbgelaufeneKommentareFuerUmfragen();
		
		Mockito.verify(kommentarRepository, times(anzahl)).deleteByLink(linkErwartet);
	}
	
	private List<KommentarDB> erstelleKommentarDBListe(int anzahl) {
		List<KommentarDB> kommentarDBs = new ArrayList<>();
		IntStream.range(1, anzahl + 1).forEach(kommentarDBNummer -> {
			KommentarDB kommentarDB = new KommentarDB();
			kommentarDB.setLink(link);
			kommentarDB.setErstellungsdatum(erstellungsdatum.plusMinutes(kommentarDBNummer));
			kommentarDB.setPseudonym(pseudonym + kommentarDBNummer);
			kommentarDB.setInhalt(inhalt + kommentarDBNummer);
			kommentarDBs.add(kommentarDB);
		});
		return kommentarDBs;
	}
	
	private List<Kommentar> erstelleKommentarListe(int anzahl) {
		List<Kommentar> kommentare = new ArrayList<>();
		IntStream.range(1, anzahl + 1).forEach(kommentarNummer -> {
			Kommentar kommentar = new Kommentar();
			kommentar.setInhalt(inhalt + kommentarNummer);
			kommentar.setLink(link);
			kommentar.setPseudonym(pseudonym + kommentarNummer);
			kommentar.setErstellungsdatum(erstellungsdatum.plusMinutes(kommentarNummer));
			kommentare.add(kommentar);
		});
		return kommentare;
	}
	
	private List<TerminfindungDB> erstelleTerminfindungDBListe(int anzahl) {
		List<TerminfindungDB> terminfindungDBs = new ArrayList<>();
		IntStream.range(1, anzahl + 1).forEach(terminDBNummer -> {
			TerminfindungDB terminfindungDB = new TerminfindungDB();
			terminfindungDB.setBeschreibung(beschreibung + terminDBNummer);
			terminfindungDB.setEinmaligeAbstimmung(false);
			terminfindungDB.setErgebnis(ergebnisTermin);
			terminfindungDB.setErsteller(ersteller);
			terminfindungDB.setFrist(frist);
			terminfindungDB.setGruppeId(gruppeId);
			terminfindungDB.setLink(link + terminDBNummer);
			terminfindungDB.setLoeschdatum(loeschdatum);
			terminfindungDB.setModus(Modus.GRUPPE);
			terminfindungDB.setOrt(ort + terminDBNummer);
			terminfindungDB.setTermin(ergebnisTermin);
			terminfindungDB.setTitel(titel + terminDBNummer);
			terminfindungDBs.add(terminfindungDB);
		});
		return terminfindungDBs;
	}
	
	private List<UmfrageDB> erstelleUmfrageDBListe(int anzahl) {
		List<UmfrageDB> umfrageDBs = new ArrayList<>();
		IntStream.range(1, anzahl + 1).forEach(umfrageDBNummer -> {
			UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setAuswahlmoeglichkeit(ergebnisUmfrage + umfrageDBNummer);
			umfrageDB.setBeschreibung(beschreibung + umfrageDBNummer);
			umfrageDB.setErgebnis(ergebnisUmfrage + umfrageDBNummer);
			umfrageDB.setErsteller(ersteller);
			umfrageDB.setFrist(frist);
			umfrageDB.setGruppeId(gruppeId);
			umfrageDB.setLink(link + umfrageDBNummer);
			umfrageDB.setLoeschdatum(loeschdatum);
			umfrageDB.setMaxAntwortAnzahl(maxAntwortAnzahl);
			umfrageDB.setModus(Modus.GRUPPE);
			umfrageDB.setTitel(titel + umfrageDBNummer);
			umfrageDBs.add(umfrageDB);
		});
		return umfrageDBs;
	}
	
}
