package mops.termine2.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import mops.termine2.database.UmfrageRepository;
import mops.termine2.models.Umfrage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

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
	
	private Umfrage erstelleBeispielUmfrage(int anzahl) {
		Umfrage umfrage = new Umfrage();
		umfrage.setBeschreibung("Tolle Beschreibung");
		umfrage.setErsteller("Me");
		umfrage.setFrist(LocalDateTime.now().plusWeeks(2));
		umfrage.setGruppe("FIXLATER");
		umfrage.setLink("BruderJakob");
		umfrage.setLoeschdatum(LocalDateTime.now().plusWeeks(2).plusMonths(2));
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
