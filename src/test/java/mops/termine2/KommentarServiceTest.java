package mops.termine2;

import mops.termine2.database.KommentarRepository;
import mops.termine2.models.Kommentar;
import mops.termine2.services.KommentarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class KommentarServiceTest {
	
	private transient KommentarService service;
	
	private transient KommentarRepository repository;
	
	private transient String inhalt = "kommentar";
	
	private transient String link = "link";
	
	private transient String pseudonym = "pseudonym";
	
	@BeforeEach
	public void setUp() {
		repository = mock(KommentarRepository.class);
		service = new KommentarService(repository);
	}
	
	@Test
	public void saveEinenKommmentar() {
		int kommentarNummer = 1;
		Kommentar kommentar = erstelleKommentar(kommentarNummer);
		
		service.save(kommentar);
		
		Mockito.verify(repository, times(kommentarNummer)).save(any());
		
	}
	
	@Test
	public void saveDreiKommentare() {
		int kommentarAnzahl = 3;
		List<Kommentar> kommentare = new ArrayList<>();
		IntStream.range(1, kommentarAnzahl + 1).forEach(kommentarNummer -> {
			kommentare.add(erstelleKommentar(kommentarNummer));
		});
		
		IntStream.range(1, kommentarAnzahl + 1).forEach(kommentarNummer -> {
			service.save(kommentare.get(kommentarNummer - 1));
		});
		
		Mockito.verify(repository, times(kommentarAnzahl)).save(any());
		
	}
	
	private Kommentar erstelleKommentar(int kommentarNummer) {
		Kommentar kommentar = new Kommentar();
		kommentar.setInhalt(inhalt + kommentarNummer);
		kommentar.setLink(link);
		kommentar.setPseudonym(pseudonym + kommentarNummer);
		kommentar.setErstellungsdatum(LocalDateTime.now());
		return kommentar;
	}
	
}
