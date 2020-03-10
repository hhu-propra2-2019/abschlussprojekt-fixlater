package mops.termine2;

import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.enums.Modus;
import mops.termine2.models.Terminfindung;
import mops.termine2.services.TerminfindungService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class TerminfindungServiceTest {
	
	TerminfindungService service;
	
	TerminfindungRepository repository;
	
	@BeforeEach
	public void setup() {
		repository = mock(TerminfindungRepository.class);
		this.service = new TerminfindungService(repository);
	}
	
	@Test
	public void saveSingleTerminfndungMitFuenfVorschlaegen() {
		int terminAnzahl = 5;
		Terminfindung termine = erstelleBeispielTermin(terminAnzahl);
		service.save(termine);
		Mockito.verify(repository, times(terminAnzahl)).save(any());
	}
	
	@Test
	public void saveSingleTerminfndungMitZehnVorschlaegen() {
		int terminAnzahl = 10;
		Terminfindung termine = erstelleBeispielTermin(terminAnzahl);
		service.save(termine);
		Mockito.verify(repository, times(terminAnzahl)).save(any());
	}
	
	@Test
	public void saveSingleTerminfndungMit1000Vorschlaegen() {
		int terminAnzahl = 1000;
		Terminfindung termine = erstelleBeispielTermin(terminAnzahl);
		service.save(termine);
		Mockito.verify(repository, times(terminAnzahl)).save(any());
	}
	
	@Test
	public void loadTerminfindungByLinkMitVierVorschlaegen() {
		int anzahl = 4;
		List<TerminfindungDB> terminfindungDBs = erstelleTerminfindungDBListeFuerEineTerminfindungMitModusGRUPPE(anzahl);
		when(repository.findByLink("alleMeineEntchen")).thenReturn(terminfindungDBs);
		Terminfindung ergebnis = service.loadByLink("alleMeineEntchen");
		
		List<LocalDateTime> enthalteneVorschlaege = erstelleVorschlaege(4);
		assertThat(ergebnis.getTitel()).isEqualTo("Titel fuer Dummies");
		assertThat(ergebnis.getBeschreibung()).isEqualTo("Ich mag Zuege");
		assertThat(ergebnis.getGruppe()).isEqualTo("fixlater");
		assertThat(ergebnis.getErsteller()).isEqualTo("reallyuselesscodeANDMarcel297");
		assertThat(ergebnis.getFrist()).isEqualTo(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLoeschdatum()).isEqualTo(LocalDateTime.of(1, 3, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLink()).isEqualTo("alleMeineEntchen");
		assertThat(ergebnis.getVorschlaege()).isEqualTo(enthalteneVorschlaege);
	}
	
	@Test
	public void loadTerminfindungenByErstellerEineTerminfindungVierVorschlaege() {
		int anzahl = 4;
		List<TerminfindungDB> terminfindungDBs = erstelleTerminfindungDBListeFuerEineTerminfindungMitModusGRUPPE(anzahl);
		List<String> links = new ArrayList<>();
		links.add("alleMeineEntchen");
		when(repository.findLinkByErsteller("reallyuselesscodeANDMarcel297")).thenReturn(links);
		when(repository.findByLink("alleMeineEntchen")).thenReturn(terminfindungDBs);
		Terminfindung ergebnis = service.loadByErsteller("reallyuselesscodeANDMarcel297").get(0);
		
		List<LocalDateTime> enthalteneVorschlaege = erstelleVorschlaege(4);
		assertThat(ergebnis.getTitel()).isEqualTo("Titel fuer Dummies");
		assertThat(ergebnis.getBeschreibung()).isEqualTo("Ich mag Zuege");
		assertThat(ergebnis.getGruppe()).isEqualTo("fixlater");
		assertThat(ergebnis.getErsteller()).isEqualTo("reallyuselesscodeANDMarcel297");
		assertThat(ergebnis.getFrist()).isEqualTo(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLoeschdatum()).isEqualTo(LocalDateTime.of(1, 3, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLink()).isEqualTo("alleMeineEntchen");
		assertThat(ergebnis.getVorschlaege()).isEqualTo(enthalteneVorschlaege);
	}
	
	
	private Terminfindung erstelleBeispielTermin(int anzahlTermine) {
		Terminfindung termine = new Terminfindung();
		termine.setBeschreibung("Ich mag Zuege");
		termine.setErsteller("reallyuselesscodeANDMarcel297");
		
		LocalDateTime frist = LocalDateTime.now().plusWeeks(2);
		termine.setLoeschdatum(frist.plusMonths(2));
		termine.setFrist(frist);
		termine.setGruppe("fixlater");
		termine.setLink("alleMeineEntchen");
		termine.setOrt("25.13.U1.24");
		termine.setTitel("Titel fuer Dummies");
		List<LocalDateTime> terminVorschlaege = erstelleVorschlaege(anzahlTermine);
		termine.setVorschlaege(terminVorschlaege);
		return termine;
	}
	
	private List<LocalDateTime> erstelleVorschlaege(int anzahlTermine) {
		List<LocalDateTime> terminVorschleage = new ArrayList<>();
		for (int i = 0; i < anzahlTermine; i++) {
			LocalDateTime termin = LocalDateTime.of(LocalDate.of(1, 1, 1),
					LocalTime.of(1, 1, 1, 1)).plusDays(i);
			
			terminVorschleage.add(termin);
		}
		return terminVorschleage;
	}
	
	public List<TerminfindungDB> erstelleTerminfindungDBListeFuerEineTerminfindungMitModusGRUPPE(int anzahlTermine) {
		List<TerminfindungDB> terminfindungDBs = new ArrayList<>();
		List<LocalDateTime> terminVorschlaege = erstelleVorschlaege(anzahlTermine);
		
		for (LocalDateTime termin : terminVorschlaege) {
			TerminfindungDB terminfindungDB = new TerminfindungDB();
			terminfindungDB.setTitel("Titel fuer Dummies");
			terminfindungDB.setOrt("25.13.U1.24");
			terminfindungDB.setErsteller("reallyuselesscodeANDMarcel297");
			terminfindungDB.setFrist(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1));
			terminfindungDB.setLoeschdatum(LocalDateTime.of(1, 3, 1, 1, 1, 1, 1));
			terminfindungDB.setLink("alleMeineEntchen");
			terminfindungDB.setBeschreibung("Ich mag Zuege");
			terminfindungDB.setGruppe("fixlater");
			terminfindungDB.setTermin(termin);
			terminfindungDB.setModus(Modus.GRUPPE);
			
			terminfindungDBs.add(terminfindungDB);
		}
		return terminfindungDBs;
	}
	
}
