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
	
	private transient TerminfindungService service;
	
	private transient TerminfindungRepository repository;
	
	private transient String link = "alleMeineEntchen";
	
	private transient String ersteller = "reallyuselesscodeANDMarcel297";
	
	private transient String beschreibung = "Ich mag Zuege";
	
	private transient String gruppe = "fixlater";
	
	private transient String ort = "25.13.U1.24";
	
	private transient String titel = "Titel fuer dummies";
	
	@BeforeEach
	public void setUp() {
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
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = erstelleTerminfindungDBListeFuerEineTerminfindungModusGruppe(anzahl);
		when(repository.findByLink(link)).thenReturn(terminfindungDBs);
		Terminfindung ergebnis = service.loadByLink(link);
		
		List<LocalDateTime> enthalteneVorschlaege = erstelleVorschlaege(anzahl);
		assertThat(ergebnis.getTitel()).isEqualTo(titel);
		assertThat(ergebnis.getBeschreibung()).isEqualTo(beschreibung);
		assertThat(ergebnis.getGruppe()).isEqualTo(gruppe);
		assertThat(ergebnis.getErsteller()).isEqualTo(ersteller);
		assertThat(ergebnis.getFrist()).isEqualTo(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLoeschdatum()).isEqualTo(LocalDateTime.of(1, 3, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLink()).isEqualTo(link);
		assertThat(ergebnis.getVorschlaege()).isEqualTo(enthalteneVorschlaege);
	}
	
	@Test
	public void loadTerminfindungByLinkMit14Vorschlaegen() {
		int anzahl = 14;
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = erstelleTerminfindungDBListeFuerEineTerminfindungModusGruppe(anzahl);
		when(repository.findByLink(link)).thenReturn(terminfindungDBs);
		Terminfindung ergebnis = service.loadByLink(link);
		
		List<LocalDateTime> enthalteneVorschlaege = erstelleVorschlaege(anzahl);
		assertThat(ergebnis.getTitel()).isEqualTo(titel);
		assertThat(ergebnis.getBeschreibung()).isEqualTo(beschreibung);
		assertThat(ergebnis.getGruppe()).isEqualTo(gruppe);
		assertThat(ergebnis.getErsteller()).isEqualTo(ersteller);
		assertThat(ergebnis.getFrist()).isEqualTo(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLoeschdatum()).isEqualTo(LocalDateTime.of(1, 3, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLink()).isEqualTo(link);
		assertThat(ergebnis.getVorschlaege()).isEqualTo(enthalteneVorschlaege);
	}
	
	
	@Test
	public void loadTerminfindungByLinkNichtExistend() {
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = new ArrayList<>();
		when(repository.findByLink(link)).thenReturn(terminfindungDBs);
		Terminfindung ergebnis = service.loadByLink(link);
		assertThat(ergebnis).isEqualTo(null);
	}
	
	@Test
	public void loadTerminfindungenByErstelleNichtExistent() {
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = new ArrayList<>();
		List<String> links = new ArrayList<>();
		when(repository.findLinkByErsteller(ersteller)).thenReturn(links);
		when(repository.findByLink(link)).thenReturn(terminfindungDBs);
		List<Terminfindung> ergebnise = service.loadByErsteller(ersteller);
		
		assertThat(ergebnise).isEqualTo(null);
	}
	
	@Test
	public void loadTerminfindungenByErstellerEineTerminfindungVierVorschlaege() {
		int anzahl = 4;
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = erstelleTerminfindungDBListeFuerEineTerminfindungModusGruppe(anzahl);
		List<String> links = new ArrayList<>();
		links.add(link);
		when(repository.findLinkByErsteller(ersteller)).thenReturn(links);
		when(repository.findByLink(link)).thenReturn(terminfindungDBs);
		Terminfindung ergebnis = service.loadByErsteller(ersteller).get(0);
		
		List<LocalDateTime> enthalteneVorschlaege = erstelleVorschlaege(anzahl);
		assertThat(ergebnis.getTitel()).isEqualTo(titel);
		assertThat(ergebnis.getBeschreibung()).isEqualTo(beschreibung);
		assertThat(ergebnis.getGruppe()).isEqualTo(gruppe);
		assertThat(ergebnis.getErsteller()).isEqualTo(ersteller);
		assertThat(ergebnis.getFrist()).isEqualTo(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLoeschdatum()).isEqualTo(LocalDateTime.of(1, 3, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLink()).isEqualTo(link);
		assertThat(ergebnis.getVorschlaege()).isEqualTo(enthalteneVorschlaege);
		assertThat(ergebnis.getOrt()).isEqualTo(ort);
	}
	
	@Test
	public void loadTerminfindungenByGruppeNichtExistent() {
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = new ArrayList<>();
		List<String> links = new ArrayList<>();
		when(repository.findLinkByGruppe(gruppe)).thenReturn(links);
		when(repository.findByLink(link)).thenReturn(terminfindungDBs);
		List<Terminfindung> ergebnise = service.loadByGruppe(gruppe);
		
		assertThat(ergebnise).isEqualTo(null);
	}
	
	@Test
	public void loadTerminfindungenByGrupperEineTerminfindung7Vorschlaege() {
		int anzahl = 7;
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = erstelleTerminfindungDBListeFuerEineTerminfindungModusGruppe(anzahl);
		List<String> links = new ArrayList<>();
		links.add(link);
		when(repository.findLinkByGruppe(gruppe)).thenReturn(links);
		when(repository.findByLink(link)).thenReturn(terminfindungDBs);
		Terminfindung ergebnis = service.loadByGruppe(gruppe).get(0);
		
		List<LocalDateTime> enthalteneVorschlaege = erstelleVorschlaege(anzahl);
		assertThat(ergebnis.getTitel()).isEqualTo(titel);
		assertThat(ergebnis.getBeschreibung()).isEqualTo(beschreibung);
		assertThat(ergebnis.getGruppe()).isEqualTo(gruppe);
		assertThat(ergebnis.getErsteller()).isEqualTo(ersteller);
		assertThat(ergebnis.getFrist()).isEqualTo(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLoeschdatum()).isEqualTo(LocalDateTime.of(1, 3, 1, 1, 1, 1, 1));
		assertThat(ergebnis.getLink()).isEqualTo(link);
		assertThat(ergebnis.getVorschlaege()).isEqualTo(enthalteneVorschlaege);
		assertThat(ergebnis.getOrt()).isEqualTo(ort);
	}
	
	
	private Terminfindung erstelleBeispielTermin(int anzahlTermine) {
		Terminfindung termine = new Terminfindung();
		termine.setBeschreibung(beschreibung);
		termine.setErsteller(ersteller);
		
		LocalDateTime frist = LocalDateTime.now().plusWeeks(2);
		termine.setLoeschdatum(frist.plusMonths(2));
		termine.setFrist(frist);
		termine.setGruppe(gruppe);
		termine.setLink(link);
		termine.setOrt(ort);
		termine.setTitel(titel);
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
	
	private List<TerminfindungDB> erstelleTerminfindungDBListeFuerEineTerminfindungModusGruppe(int anzahlTermine) {
		List<TerminfindungDB> terminfindungDBs = new ArrayList<>();
		List<LocalDateTime> terminVorschlaege = erstelleVorschlaege(anzahlTermine);
		
		for (LocalDateTime termin : terminVorschlaege) {
			TerminfindungDB terminfindungDB = new TerminfindungDB();
			terminfindungDB.setTitel(titel);
			terminfindungDB.setOrt(ort);
			terminfindungDB.setErsteller(ersteller);
			terminfindungDB.setFrist(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1));
			terminfindungDB.setLoeschdatum(LocalDateTime.of(1, 3, 1, 1, 1, 1, 1));
			terminfindungDB.setLink(link);
			terminfindungDB.setBeschreibung(beschreibung);
			terminfindungDB.setGruppe(gruppe);
			terminfindungDB.setTermin(termin);
			terminfindungDB.setModus(Modus.GRUPPE);
			
			terminfindungDBs.add(terminfindungDB);
		}
		
		return terminfindungDBs;
		
	}
	
}
