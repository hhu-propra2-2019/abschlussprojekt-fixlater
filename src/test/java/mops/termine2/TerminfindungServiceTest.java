package mops.termine2;

import mops.termine2.database.TerminfindungAntwortRepository;
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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class TerminfindungServiceTest {
	
	
	private transient List<String> linkListe = new ArrayList<>(
			Arrays.asList("alleMeineEntchen", "haenzchenKlein", "bruderJakob", "derMondIstAufgegangen"));
	
	private transient List<String> erstellerListe = new ArrayList<>(
			Arrays.asList("Leon", "Loen", "Thomas", "Anthon"));
	
	private transient List<String> gruppenListe = new ArrayList<>(
			Arrays.asList("G1", "G2", "G3", null));
	
	private transient List<String> titelListe = new ArrayList<>(
			Arrays.asList("Titel1", "Titel2", "Titel3", "Titel4"));
	
	private transient List<String> beschreibungsListe = new ArrayList<>(
			Arrays.asList("B1", "B2", "B3", "B4"));
	
	private transient List<String> ortListe = new ArrayList<>(
			Arrays.asList("O1", "O2", "O3", "O4"));
	
	private transient LocalDateTime loeschdatum = LocalDateTime.of(1, 3, 1, 1, 1, 1, 1);
	
	private transient LocalDateTime frist = LocalDateTime.of(1, 1, 1, 1, 1, 1, 1);
	
	private transient List<LocalDateTime> loeschdatumListe = new ArrayList<>(
			Arrays.asList(loeschdatum, loeschdatum, loeschdatum, loeschdatum));
	
	private transient List<LocalDateTime> fristListe = new ArrayList<>(
			Arrays.asList(frist, frist, frist, frist));
	
	
	private transient TerminfindungService service;
	
	private transient TerminfindungRepository terminRepository;
	
	private transient TerminfindungAntwortRepository antwortRepository;
	
	@BeforeEach
	public void setUp() {
		terminRepository = mock(TerminfindungRepository.class);
		antwortRepository = mock(TerminfindungAntwortRepository.class);
		service = new TerminfindungService(terminRepository, antwortRepository);
	}
	
	@Test
	public void saveSingleTerminfndungMit5VorschlaegenModusGruppe() {
		int terminAnzahl = 5;
		Terminfindung termine = erstelleBeispielTerminfindung(terminAnzahl);
		service.save(termine);
		Mockito.verify(terminRepository, times(terminAnzahl)).save(any());
	}
	
	@Test
	public void saveSingleTerminfndungMit5VorschlaegenModusLink() {
		int terminAnzahl = 5;
		Terminfindung termine = erstelleBeispielTerminfindung(3, terminAnzahl);
		service.save(termine);
		Mockito.verify(terminRepository, times(terminAnzahl)).save(any());
	}
	
	@Test
	public void saveSingleTerminfndungMit10Vorschlaegen() {
		int terminAnzahl = 10;
		Terminfindung termine = erstelleBeispielTerminfindung(terminAnzahl);
		service.save(termine);
		Mockito.verify(terminRepository, times(terminAnzahl)).save(any());
	}
	
	@Test
	public void saveSingleTerminfndungMit1000Vorschlaegen() {
		int terminAnzahl = 1000;
		Terminfindung termine = erstelleBeispielTerminfindung(terminAnzahl);
		service.save(termine);
		Mockito.verify(terminRepository, times(terminAnzahl)).save(any());
	}
	
	@Test
	public void loadTerminfindungByLinkMit4Vorschlaegen() {
		int anzahl = 4;
		int dummie = 0;
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = erstelleTerminfindungDBListeFuerEineTerminfindung(dummie, anzahl);
		when(terminRepository.findByLink(linkListe.get(dummie))).thenReturn(terminfindungDBs);
		Terminfindung ergebnis = service.loadByLinkMitTerminen(linkListe.get(dummie));
		Terminfindung erwartet = erstelleBeispielTerminfindung(dummie, anzahl);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadTerminfindungByLinkMit14Vorschlaegen() {
		int anzahl = 14;
		int dummie = 0;
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = erstelleTerminfindungDBListeFuerEineTerminfindung(dummie, anzahl);
		when(terminRepository.findByLink(linkListe.get(dummie))).thenReturn(terminfindungDBs);
		Terminfindung ergebnis = service.loadByLinkMitTerminen(linkListe.get(dummie));
		Terminfindung erwartet = erstelleBeispielTerminfindung(dummie, anzahl);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	
	@Test
	public void loadTerminfindungByLinkKeineTreffer() {
		int dummie = 0;
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = new ArrayList<>();
		when(terminRepository.findByLink(linkListe.get(dummie))).thenReturn(terminfindungDBs);
		Terminfindung ergebnis = service.loadByLinkMitTerminen(linkListe.get(dummie));
		assertThat(ergebnis).isEqualTo(null);
	}
	
	
	@Test
	public void loadTerminfindungenByErstellerKeineTreffer() {
		int dummie = 0;
		List<TerminfindungDB> dbs = new ArrayList<>();
		when(terminRepository.findByErsteller(erstellerListe.get(dummie))).thenReturn(dbs);
		List<Terminfindung> ergebnisse = service.loadByErstellerOhneTermine(erstellerListe.get(dummie));
		
		assertThat(ergebnisse.isEmpty()).isTrue();
	}
	
	@Test
	public void loadTerminfindungenByErstellerEineTerminfindungVierVorschlaege() {
		int anzahl = 4;
		int dummie = 0;
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = erstelleTerminfindungDBListeFuerEineTerminfindung(dummie, anzahl);
		when(terminRepository.findByErsteller(erstellerListe.get(dummie))).thenReturn(terminfindungDBs);
		
		Terminfindung ergebnis = service.loadByErstellerOhneTermine(erstellerListe.get(dummie)).get(0);
		Terminfindung erwartet = erstelleBeispielTerminfindungOhneTermine(0);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadTerminfindungenByGruppeKeineTreffer() {
		int dummie = 0;
		List<TerminfindungDB> terminfindungDBs = new ArrayList<>();
		when(terminRepository.findByErsteller(erstellerListe.get(dummie))).thenReturn(terminfindungDBs);
		List<Terminfindung> ergebnisse = service.loadByErstellerOhneTermine(erstellerListe.get(dummie));
		
		assertThat(ergebnisse.isEmpty()).isTrue();
	}
	
	
	@Test
	public void loadTerminfindungenByGruppeEineTerminfindungVierVorschlaege() {
		int anzahl = 4;
		int dummie = 0;
		List<TerminfindungDB> terminfindungDBs;
		terminfindungDBs = erstelleTerminfindungDBListeFuerEineTerminfindung(dummie, anzahl);
		when(terminRepository.findByGruppe(erstellerListe.get(dummie))).thenReturn(terminfindungDBs);
		
		Terminfindung ergebnis = service.loadByGruppeOhneTermine(erstellerListe.get(dummie)).get(0);
		Terminfindung erwartet = erstelleBeispielTerminfindungOhneTermine(0);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void load2TerminfindungenByGruppe() {
		int dummie1 = 0;
		int dummie2 = 1;
		List<TerminfindungDB> terminfindungDBs = new ArrayList<>();
		List<TerminfindungDB> terminfindungDBs1;
		List<TerminfindungDB> terminfindungDBs2;
		terminfindungDBs1 = erstelleTerminfindungDBListeFuerEineTerminfindungOhneTermine(dummie1);
		terminfindungDBs2 = erstelleTerminfindungDBListeFuerEineTerminfindungOhneTermine(dummie2);
		terminfindungDBs.addAll(terminfindungDBs1);
		terminfindungDBs.addAll(terminfindungDBs2);
		
		when(terminRepository.findByGruppe(gruppenListe.get(0))).thenReturn(terminfindungDBs);
		
		List<Terminfindung> erwartet = new ArrayList<>(
				Arrays.asList(erstelleBeispielTerminfindungOhneTermine(dummie1),
						erstelleBeispielTerminfindungOhneTermine(dummie2)
				)
		);
		
		List<Terminfindung> ergebnis = service.loadByGruppeOhneTermine(gruppenListe.get(0));
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void loadByBenutzer() {
		int dummie1 = 0;
		int dummie2 = 1;
		String benutzer = "benutzer";
		List<TerminfindungDB> terminfindungDBs = new ArrayList<>();
		List<TerminfindungDB> terminfindungDBs1;
		List<TerminfindungDB> terminfindungDBs2;
		terminfindungDBs1 = erstelleTerminfindungDBListeFuerEineTerminfindungOhneTermine(dummie1);
		terminfindungDBs2 = erstelleTerminfindungDBListeFuerEineTerminfindungOhneTermine(dummie2);
		terminfindungDBs.addAll(terminfindungDBs1);
		terminfindungDBs.addAll(terminfindungDBs2);
		
		when(antwortRepository.findTerminindungDbByBenutzer(benutzer)).thenReturn(terminfindungDBs);
		
		List<Terminfindung> erwartet = new ArrayList<>(
				Arrays.asList(erstelleBeispielTerminfindungOhneTermine(dummie1),
						erstelleBeispielTerminfindungOhneTermine(dummie2)
				)
		);
		
		List<Terminfindung> ergebnis = service.loadAllBenutzerHatAbgestimmtOhneTermine(benutzer);
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	private Terminfindung erstelleBeispielTerminfindung(int dummie, int anzahlTermine) {
		Terminfindung termine = new Terminfindung();
		termine.setBeschreibung(beschreibungsListe.get(dummie));
		termine.setErsteller(erstellerListe.get(dummie));
		
		termine.setLoeschdatum(loeschdatumListe.get(dummie));
		termine.setFrist(fristListe.get(dummie));
		termine.setGruppe(gruppenListe.get(dummie));
		termine.setLink(linkListe.get(dummie));
		termine.setOrt(ortListe.get(dummie));
		termine.setTitel(titelListe.get(dummie));
		List<LocalDateTime> terminVorschlaege = erstelleVorschlaege(anzahlTermine);
		termine.setVorschlaege(terminVorschlaege);
		return termine;
	}
	
	private Terminfindung erstelleBeispielTerminfindung(int anzahlTermine) {
		return erstelleBeispielTerminfindung(0, anzahlTermine);
	}
	
	private Terminfindung erstelleBeispielTerminfindungOhneTermine(int dummie) {
		Terminfindung termine = new Terminfindung();
		termine.setBeschreibung(beschreibungsListe.get(dummie));
		termine.setErsteller(erstellerListe.get(dummie));
		
		termine.setLoeschdatum(loeschdatumListe.get(dummie));
		termine.setFrist(fristListe.get(dummie));
		termine.setGruppe(gruppenListe.get(dummie));
		termine.setLink(linkListe.get(dummie));
		termine.setOrt(ortListe.get(dummie));
		termine.setTitel(titelListe.get(dummie));
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
	
	private List<TerminfindungDB> erstelleTerminfindungDBListeFuerEineTerminfindung(
			int dummie, int anzahlTermine) {
		List<TerminfindungDB> terminfindungDBs = new ArrayList<>();
		List<LocalDateTime> terminVorschlaege = erstelleVorschlaege(anzahlTermine);
		
		for (LocalDateTime termin : terminVorschlaege) {
			TerminfindungDB terminfindungDB = new TerminfindungDB();
			terminfindungDB.setTitel(titelListe.get(dummie));
			terminfindungDB.setOrt(ortListe.get(dummie));
			terminfindungDB.setErsteller(erstellerListe.get(dummie));
			terminfindungDB.setFrist(fristListe.get(dummie));
			terminfindungDB.setLoeschdatum(loeschdatumListe.get(dummie));
			terminfindungDB.setLink(linkListe.get(dummie));
			terminfindungDB.setBeschreibung(beschreibungsListe.get(dummie));
			terminfindungDB.setGruppe(gruppenListe.get(dummie));
			terminfindungDB.setTermin(termin);
			if (terminfindungDB.getGruppe() != null) {
				terminfindungDB.setModus(Modus.GRUPPE);
			} else {
				terminfindungDB.setModus(Modus.LINK);
			}
			
			terminfindungDBs.add(terminfindungDB);
		}
		
		return terminfindungDBs;
		
	}
	
	private List<TerminfindungDB> erstelleTerminfindungDBListeFuerEineTerminfindungOhneTermine(int dummie) {
		List<TerminfindungDB> terminfindungDBs = new ArrayList<>();
		TerminfindungDB terminfindungDB = new TerminfindungDB();
		terminfindungDB.setTitel(titelListe.get(dummie));
		terminfindungDB.setOrt(ortListe.get(dummie));
		terminfindungDB.setErsteller(erstellerListe.get(dummie));
		terminfindungDB.setFrist(fristListe.get(dummie));
		terminfindungDB.setLoeschdatum(loeschdatumListe.get(dummie));
		terminfindungDB.setLink(linkListe.get(dummie));
		terminfindungDB.setBeschreibung(beschreibungsListe.get(dummie));
		terminfindungDB.setGruppe(gruppenListe.get(dummie));
		if (terminfindungDB.getGruppe() != null) {
			terminfindungDB.setModus(Modus.GRUPPE);
		} else {
			terminfindungDB.setModus(Modus.LINK);
		}
		
		
		terminfindungDBs.add(terminfindungDB);
		return terminfindungDBs;
	}
}
