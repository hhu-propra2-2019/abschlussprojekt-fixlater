package mops.termine2.services;

import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Modus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LinkServiceTest {
	
	private transient LinkService linkService;
	
	private transient TerminfindungRepository terminfindungRepo;
	
	private transient UmfrageRepository umfrageRepo;
	
	private transient String ersteller = "KathiundJulia";
	
	private transient String beschreibung = "SchokoBons";
	
	private transient String gruppeId = "1";
	
	private transient String ort = "hoersaal5b";
	
	private transient String titel = "Snjäckz";
	
	private transient String link = "link";
	
	@BeforeEach
	public void setUp() {
		terminfindungRepo = mock(TerminfindungRepository.class);
		umfrageRepo = mock(UmfrageRepository.class);
		linkService = new LinkService(terminfindungRepo, umfrageRepo);
	}
	
	@Test
	public void pruefeEindeutigkeitLinkNochNichtVorhanden() {
		when(terminfindungRepo.findByLink(link)).thenReturn(new ArrayList<>());
		when(umfrageRepo.findByLink(link)).thenReturn(new ArrayList<>());
		
		Boolean eindeutig = linkService.pruefeEindeutigkeitLink(link);
		
		assertThat(eindeutig).isEqualTo(true);
	}
	
	@Test
	public void pruefeEindeutigkeitLinkGehoertZuTerminfindung() {
		when(terminfindungRepo.findByLink(link))
			.thenReturn(erstelleTerminfindungDBListeFuerGruppe(link));
		when(umfrageRepo.findByLink(link)).thenReturn(new ArrayList<>());
		
		Boolean eindeutig = linkService.pruefeEindeutigkeitLink(link);
		
		assertThat(eindeutig).isEqualTo(false);
		
	}
	
	@Test
	public void pruefeEindeutigkeitLinkGehoertZuUmfrage() {
		when(umfrageRepo.findByLink(link))
			.thenReturn(erstelleUmfrageDBListeFuerGruppe(link));
		when(terminfindungRepo.findByLink(link)).thenReturn(new ArrayList<>());
		
		Boolean eindeutig = linkService.pruefeEindeutigkeitLink(link);
		
		assertThat(eindeutig).isEqualTo(false);
		
	}
	
	@Test
	public void generiertEindeutigenLink() {
		String uuidLinkTerminfindung = "ef2b999e-da9e-40af-a3c7-bc6ae00a9d70";
		String uuidLinkUmfrage = "195e3001-4bce-41fe-9cf9-138602d9ba2d";
		when(terminfindungRepo.findByLink(uuidLinkTerminfindung))
			.thenReturn(erstelleTerminfindungDBListeFuerGruppe(uuidLinkTerminfindung));
		when(umfrageRepo.findByLink(uuidLinkUmfrage))
			.thenReturn(erstelleUmfrageDBListeFuerGruppe(uuidLinkUmfrage));
		
		String generierterLink = linkService.generiereEindeutigenLink();
		
		assertThat(generierterLink).isNotEqualTo(uuidLinkTerminfindung);
		assertThat(generierterLink).isNotEqualTo(uuidLinkUmfrage);
		
	}
	
	@Test
	public void testValidLink() {
		Boolean bool = linkService.isLinkValid("baum");
		assertThat(bool).isTrue();
	}
	
	@Test
	public void testValidLinkWithDash() {
		Boolean bool = linkService.isLinkValid("-b-a-u-m-123");
		assertThat(bool).isTrue();
	}
	
	@Test
	public void testInvalidLink() {
		Boolean bool = linkService.isLinkValid("bähh");
		assertThat(bool).isFalse();
	}
	
	private List<TerminfindungDB> erstelleTerminfindungDBListeFuerGruppe(String link) {
		List<TerminfindungDB> terminfindungDBs = new ArrayList<>();
		List<LocalDateTime> terminVorschlaege = Arrays.asList(LocalDateTime.now(),
			LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
		
		for (LocalDateTime termin : terminVorschlaege) {
			TerminfindungDB terminfindungDB = new TerminfindungDB();
			terminfindungDB.setTitel(titel);
			terminfindungDB.setOrt(ort);
			terminfindungDB.setErsteller(ersteller);
			terminfindungDB.setFrist(LocalDateTime.now().plusMonths(1));
			terminfindungDB.setLoeschdatum(LocalDateTime.now().plusMonths(2));
			terminfindungDB.setLink(link);
			terminfindungDB.setBeschreibung(beschreibung);
			terminfindungDB.setGruppeId(gruppeId);
			terminfindungDB.setTermin(termin);
			terminfindungDB.setModus(Modus.GRUPPE);
			
			terminfindungDBs.add(terminfindungDB);
		}
		
		return terminfindungDBs;
		
	}
	
	private List<UmfrageDB> erstelleUmfrageDBListeFuerGruppe(String link) {
		List<UmfrageDB> umfrageDBs = new ArrayList<>();
		List<String> optionen = Arrays.asList("option1", "option2", "option3");
		
		for (String option : optionen) {
			UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setTitel(titel);
			umfrageDB.setErsteller(ersteller);
			umfrageDB.setFrist(LocalDateTime.now().plusMonths(1));
			umfrageDB.setLoeschdatum(LocalDateTime.now().plusMonths(2));
			umfrageDB.setLink(link);
			umfrageDB.setBeschreibung(beschreibung);
			umfrageDB.setGruppeId(gruppeId);
			umfrageDB.setModus(Modus.GRUPPE);
			umfrageDB.setMaxAntwortAnzahl(3L);
			umfrageDB.setAuswahlmoeglichkeit(option);
			
			umfrageDBs.add(umfrageDB);
		}
		
		return umfrageDBs;
		
	}
}
