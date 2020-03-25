package mops.termine2;

import mops.termine2.database.KommentarRepository;
import mops.termine2.database.entities.KommentarDB;
import mops.termine2.models.Kommentar;
import mops.termine2.services.KommentarService;
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
	
	private transient KommentarRepository repository;
	
	private transient String inhalt = "kommentar";
	
	private transient String link = "link";
	
	private transient String pseudonym = "pseudonym";
	
	private transient LocalDateTime erstellungsdatum = LocalDateTime.now();
	
	@BeforeEach
	public void setUp() {
		repository = mock(KommentarRepository.class);
		service = new KommentarService(repository);
	}
	
	@Test
	public void saveEinenKommmentar() {
		int anzahl = 1;
		List<Kommentar> kommentar = erstelleKommentarListe(anzahl);
		
		service.save(kommentar.get(0));
		
		Mockito.verify(repository, times(anzahl)).save(any());
	}
	
	@Test
	public void saveDreiKommentare() {
		int anzahl = 3;
		List<Kommentar> kommentare = erstelleKommentarListe(anzahl);
		
		IntStream.range(1, anzahl + 1).forEach(kommentarNummer -> {
			service.save(kommentare.get(kommentarNummer - 1));
		});
		
		Mockito.verify(repository, times(anzahl)).save(any());
		
	}
	
	@Test
	public void loadByLinkEinenKommentar() {
		int anzahl = 1;
		List<KommentarDB> kommentarDBs = erstelleKommentarDBListe(anzahl);
		when(repository.findByLinkOrderByErstellungsdatumAsc(link)).thenReturn(kommentarDBs);
		List<Kommentar> kommentareErwartet = erstelleKommentarListe(anzahl);
		
		List<Kommentar> kommentare = service.loadByLink(link);
		
		assertThat(kommentare).isEqualTo(kommentareErwartet);
	}
	
	@Test
	public void loadByLinkDreiKommentare() {
		int anzahl = 3;
		List<KommentarDB> kommentarDBs = erstelleKommentarDBListe(anzahl);
		when(repository.findByLinkOrderByErstellungsdatumAsc(link)).thenReturn(kommentarDBs);
		List<Kommentar> kommentareErwartet = erstelleKommentarListe(anzahl);
		
		List<Kommentar> kommentare = service.loadByLink(link);
		
		assertThat(kommentare).isEqualTo(kommentareErwartet);
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
	
}
