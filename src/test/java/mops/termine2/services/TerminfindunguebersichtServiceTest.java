package mops.termine2.services;

import mops.termine2.authentication.Account;
import mops.termine2.database.BenutzerGruppeRepository;
import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.models.Terminfindung;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TerminfindunguebersichtServiceTest {
	
	private transient TerminfindunguebersichtService terminfindunguebersichtService;
	
	private transient TerminfindungService terminfindungService;
	
	private transient GruppeService gruppeService;
	
	private transient TerminfindungRepository terminfindungRepository;
	
	private transient BenutzerGruppeRepository benutzerGruppeRepository;
	
	private transient TerminfindungAntwortRepository antwortRepo;
	
	private transient TerminAntwortService terminAntwortService;
	
	@BeforeEach
	public void setUp() {
		terminfindungRepository = mock(TerminfindungRepository.class);
		antwortRepo = mock(TerminfindungAntwortRepository.class);
		benutzerGruppeRepository = mock(BenutzerGruppeRepository.class);
		
		terminfindungService = new TerminfindungService(terminfindungRepository, antwortRepo);
		terminAntwortService = new TerminAntwortService(antwortRepo, terminfindungRepository);
		gruppeService = new GruppeService(benutzerGruppeRepository);
		terminfindunguebersichtService = new TerminfindunguebersichtService(
			terminfindungService, gruppeService, terminAntwortService);
	}
	
	@Test
	public void testLoadOffeneTerminfindungenFuerBenutzer() {
		List<Integer> days = new ArrayList<>(Arrays.asList(5, -5, 1, -2));
		Account account = new Account("studentin", null, null, null);
		LocalDateTime ldt = LocalDateTime.now();
		
		BenutzerGruppeDB gruppe = new BenutzerGruppeDB();
		gruppe.setGruppe("Gruppe");
		
		List<Terminfindung> terminfindungen = new ArrayList<>();
		List<TerminfindungDB> terminfindungenDB = new ArrayList<>();
		
		for (Integer day : days) {
			Terminfindung termin = new Terminfindung();
			termin.setLink(day.toString());
			termin.setFrist(ldt.plusDays(day));
			termin.setEinmaligeAbstimmung(false);
			termin.setErgebnisVorFrist(true);
			terminfindungen.add(termin);
			
			TerminfindungDB terminDB = new TerminfindungDB();
			terminDB.setLink(day.toString());
			terminDB.setFrist(ldt.plusDays(day));
			terminDB.setEinmaligeAbstimmung(false);
			terminDB.setErgebnisVorFrist(true);
			terminfindungenDB.add(terminDB);
		}
		
		when(antwortRepo.findByBenutzerAndTerminfindungLink(account.getName(), "5")).thenReturn(
			new ArrayList<TerminfindungAntwortDB>());
		when(antwortRepo.findByBenutzerAndTerminfindungLink(account.getName(), "1")).thenReturn(
			new ArrayList<TerminfindungAntwortDB>());
		when(benutzerGruppeRepository.findByBenutzer(account.getName())).thenReturn(
			new ArrayList<>(Arrays.asList(gruppe)));
		when(terminfindungRepository.findByGruppeIdOrderByFristAsc(gruppe.getGruppeId()))
			.thenReturn(terminfindungenDB);
		
		List<Terminfindung> ergebnis =
			terminfindunguebersichtService.loadOffeneTerminfindungenFuerBenutzer(account);
		List<Terminfindung> erwartet =
			new ArrayList<>(Arrays.asList(terminfindungen.get(2), terminfindungen.get(0)));
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void testLoadAbgeschlosseneTerminfindungenFuerBenutzer() {
		List<Integer> fristTage = new ArrayList<>(Arrays.asList(5, -5, 1, -2));
		List<Integer> ergebnisTage = new ArrayList<>(Arrays.asList(1, -2, -1, 2));
		Account account = new Account("studentin", null, null, null);
		LocalDateTime ldt = LocalDateTime.now();
		
		BenutzerGruppeDB gruppe = new BenutzerGruppeDB();
		gruppe.setGruppe("Gruppe");
		
		List<Terminfindung> terminfindungen = new ArrayList<>();
		List<TerminfindungDB> terminfindungenDB = new ArrayList<>();
		
		for (int i = 0; i < fristTage.size(); i++) {
			Terminfindung termin = new Terminfindung();
			termin.setLink(fristTage.get(i).toString());
			termin.setFrist(ldt.plusDays(fristTage.get(i)));
			termin.setErgebnis(ldt.plusDays(ergebnisTage.get(i)));
			termin.setEinmaligeAbstimmung(false);
			termin.setErgebnisVorFrist(true);
			terminfindungen.add(termin);
			
			TerminfindungDB terminDB = new TerminfindungDB();
			terminDB.setLink(fristTage.get(i).toString());
			terminDB.setFrist(ldt.plusDays(fristTage.get(i)));
			terminDB.setErgebnis(ldt.plusDays(ergebnisTage.get(i)));
			terminDB.setEinmaligeAbstimmung(false);
			terminDB.setErgebnisVorFrist(true);
			terminfindungenDB.add(terminDB);
		}
		
		when(benutzerGruppeRepository.findByBenutzer(account.getName())).thenReturn(
			new ArrayList<>(Arrays.asList(gruppe)));
		when(terminfindungRepository.findByGruppeIdOrderByFristAsc(gruppe.getGruppeId()))
			.thenReturn(terminfindungenDB);
		
		List<Terminfindung> ergebnis =
			terminfindunguebersichtService.loadAbgeschlosseneTerminfindungenFuerBenutzer(account);
		List<Terminfindung> erwartet =
			new ArrayList<>(Arrays.asList(terminfindungen.get(3), terminfindungen.get(1)));
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void testLoadOffeneTerminfindungenFuerGruppe() {
		List<Integer> days = new ArrayList<>(Arrays.asList(5, -5, 1, -2, 3));
		List<Long> gruppenIds = new ArrayList<>(Arrays.asList(1L, 5L, 5L, 7L, 5L));
		Account account = new Account("studentin", null, null, null);
		LocalDateTime ldt = LocalDateTime.now();
		
		BenutzerGruppeDB gruppe = new BenutzerGruppeDB();
		gruppe.setGruppe("Gruppe");
		gruppe.setGruppeId(5L);
		
		List<Terminfindung> terminfindungen = new ArrayList<>();
		List<TerminfindungDB> terminfindungenDB = new ArrayList<>();
		
		for (int i = 0; i < days.size(); i++) {
			Terminfindung termin = new Terminfindung();
			termin.setLink(days.get(i).toString());
			termin.setFrist(ldt.plusDays(days.get(i)));
			termin.setEinmaligeAbstimmung(false);
			termin.setGruppeId(gruppenIds.get(i));
			termin.setErgebnisVorFrist(true);
			terminfindungen.add(termin);
			
			if (gruppenIds.get(i).equals(gruppe.getGruppeId())) {
				TerminfindungDB terminDB = new TerminfindungDB();
				terminDB.setLink(days.get(i).toString());
				terminDB.setFrist(ldt.plusDays(days.get(i)));
				terminDB.setEinmaligeAbstimmung(false);
				terminDB.setGruppeId(gruppenIds.get(i));
				terminDB.setErgebnisVorFrist(true);
				terminfindungenDB.add(terminDB);
			}
		}
		when(terminfindungRepository.findByGruppeIdOrderByFristAsc(gruppe.getGruppeId()))
			.thenReturn(terminfindungenDB);
		
		List<Terminfindung> ergebnis =
			terminfindunguebersichtService
				.loadOffeneTerminfindungenFuerGruppe(account, gruppe.getGruppeId());
		List<Terminfindung> erwartet =
			new ArrayList<>(Arrays.asList(terminfindungen.get(2), terminfindungen.get(4)));
		
		assertThat(ergebnis).isEqualTo(erwartet);
	}
	
	@Test
	public void testLoadAbgeschlosseneTerminfindungenFuerGruppe() {
		List<Integer> days = new ArrayList<>(Arrays.asList(5, -5, 1, -2, 3, -7));
		List<Integer> ergebnis = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
		List<Long> gruppenIds = new ArrayList<>(Arrays.asList(1L, 5L, 5L, 7L, 5L, 5L));
		Account account = new Account("studentin", null, null, null);
		LocalDateTime ldt = LocalDateTime.now();
		
		BenutzerGruppeDB gruppe = new BenutzerGruppeDB();
		gruppe.setGruppe("Gruppe");
		gruppe.setGruppeId(5L);
		
		List<Terminfindung> terminfindungen = new ArrayList<>();
		List<TerminfindungDB> terminfindungenDB = new ArrayList<>();
		
		for (int i = 0; i < days.size(); i++) {
			Terminfindung termin = new Terminfindung();
			termin.setLink(days.get(i).toString());
			termin.setFrist(ldt.plusDays(days.get(i)));
			termin.setEinmaligeAbstimmung(false);
			termin.setGruppeId(gruppenIds.get(i));
			termin.setErgebnis(ldt.plusDays(ergebnis.get(i)));
			termin.setErgebnisVorFrist(true);
			terminfindungen.add(termin);
			
			if (gruppenIds.get(i).equals(gruppe.getGruppeId())) {
				TerminfindungDB terminDB = new TerminfindungDB();
				terminDB.setLink(days.get(i).toString());
				terminDB.setFrist(ldt.plusDays(days.get(i)));
				terminDB.setEinmaligeAbstimmung(false);
				terminDB.setGruppeId(gruppenIds.get(i));
				terminDB.setErgebnis(ldt.plusDays(ergebnis.get(i)));
				terminDB.setErgebnisVorFrist(true);
				terminfindungenDB.add(terminDB);
			}
		}
		
		when(terminfindungRepository.findByGruppeIdOrderByFristAsc(gruppe.getGruppeId()))
			.thenReturn(terminfindungenDB);
		
		List<Terminfindung> result =
			terminfindunguebersichtService
				.loadAbgeschlosseneTerminfindungenFuerGruppe(account, gruppe.getGruppeId());
		List<Terminfindung> erwartet =
			new ArrayList<>(Arrays.asList(terminfindungen.get(1), terminfindungen.get(5)));
		
		assertThat(result).isEqualTo(erwartet);
	}
	
}
