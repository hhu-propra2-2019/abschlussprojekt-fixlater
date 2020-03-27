package mops.termine2.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mops.termine2.authentication.Account;
import mops.termine2.database.BenutzerGruppeRepository;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.models.Gruppe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GruppeServiceTest {
	
	private transient GruppeService gruppeService;
	
	private transient BenutzerGruppeRepository benutzerGruppeRepository;
	
	@BeforeEach
	public void setUp() {
		benutzerGruppeRepository = mock(BenutzerGruppeRepository.class);
		gruppeService = new GruppeService(benutzerGruppeRepository);
	}
	
	@Test
	public void testLoadByBenutzer() {
		Account account = new Account("studentin", "abc@def.de", null, null);
		List<String> groupNames = new ArrayList<>(Arrays.asList("Best group eva", "FIXME", "Last One :("));
		
		List<BenutzerGruppeDB> gruppenDB = new ArrayList<>();
		List<Gruppe> gruppen = new ArrayList<>();
		for (String name : groupNames) {
			BenutzerGruppeDB gruppeDB = new BenutzerGruppeDB();
			gruppeDB.setGruppe(name);
			gruppenDB.add(gruppeDB);
			
			Gruppe gruppe = new Gruppe();
			gruppe.setName(name);
			gruppen.add(gruppe);
		}
		
		when(benutzerGruppeRepository.findByBenutzer(account.getName())).thenReturn(gruppenDB);
		
		List<Gruppe> ergebnis = gruppeService.loadByBenutzer(account);
		
		for (int i = 0; i < gruppen.size(); i++) {
			assertThat(gruppen.get(i).getId()).isEqualTo(ergebnis.get(i).getId());
			assertThat(gruppen.get(i).getName()).isEqualTo(ergebnis.get(i).getName());
		}
		
	}
	
	@Test
	public void testLoadById() {
		Gruppe expected = new Gruppe();
		expected.setId("1");
		expected.setName("Test");
		
		BenutzerGruppeDB user = new BenutzerGruppeDB();
		user.setBenutzer("Hallo");
		user.setGruppe("Test");
		user.setGruppeId("1");
		user.setId(1L);
		
		when(benutzerGruppeRepository.findByGruppeId("1"))
			.thenReturn(new ArrayList<BenutzerGruppeDB>(Arrays.asList(user)));
		
		Gruppe result = gruppeService.loadByGruppeId("1");
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void testLoadByIdNoGroup() {				
		when(benutzerGruppeRepository.findByGruppeId("1"))
			.thenReturn(new ArrayList<BenutzerGruppeDB>());
		
		Gruppe result = gruppeService.loadByGruppeId("1");
		
		assertThat(result).isNull();
	}
	
	@Test
	public void testAccountInGruppe() {
		Account account = new Account("studentin", "abc@def.de", null, null);
		BenutzerGruppeDB user = new BenutzerGruppeDB();
		user.setBenutzer("studentin");
		user.setGruppe("Test");
		user.setGruppeId("1");
		user.setId(1L);
		
		when(benutzerGruppeRepository.findByBenutzerAndGruppeId("studentin", "1"))
			.thenReturn(user);
		
		boolean result = gruppeService.accountInGruppe(account, "1");
		
		assertThat(result).isEqualTo(true);
	}
	
	@Test
	public void testAccountNotInGruppe() {
		Account account = new Account("studentin", "abc@def.de", null, null);		
		when(benutzerGruppeRepository.findByBenutzerAndGruppeId("studentin", "1"))
			.thenReturn(null);
		
		boolean result = gruppeService.accountInGruppe(account, "1");
		
		assertThat(result).isEqualTo(false);
	}
	
	@Test
	public void testSortedGroupnames() {
		Gruppe g1 = new Gruppe();
		g1.setId("1");
		g1.setName("a");
		Gruppe g2 = new Gruppe();
		g2.setId("2");
		g2.setName("b");
		List<Gruppe> input = new ArrayList<Gruppe>(
			Arrays.asList(
				g2, g1
			));
		
		List<Gruppe> expected = new ArrayList<Gruppe>(
			Arrays.asList(
				g1, g2
			));
		
		List<Gruppe> result = gruppeService.sortGroupsByName(input);
		
		assertThat(result).isEqualTo(expected);		
	}
	
	@Test
	public void testAccessDeniedNull() {
		Account account = new Account("studentin", "abc@def.de", null, null);
		boolean result = gruppeService.checkGroupAccessDenied(account, null);
		assertThat(result).isEqualTo(false);
	}
	
	@Test
	public void testAccessDeniedMinus1() {
		Account account = new Account("studentin", "abc@def.de", null, null);
		boolean result = gruppeService.checkGroupAccessDenied(account, "-1");
		assertThat(result).isEqualTo(false);
	}
	
	@Test
	public void testAccessDenied() {
		Account account = new Account("studentin", "abc@def.de", null, null);		
		when(benutzerGruppeRepository.findByBenutzerAndGruppeId("studentin", "1"))
			.thenReturn(null);
		
		boolean result = gruppeService.checkGroupAccessDenied(account, "1");
		
		assertThat(result).isEqualTo(true);
	}
	
	@Test
	public void testAccessNotDenied() {
		Account account = new Account("studentin", "abc@def.de", null, null);
		BenutzerGruppeDB user = new BenutzerGruppeDB();
		user.setBenutzer("studentin");
		user.setGruppe("Test");
		user.setGruppeId("1");
		user.setId(1L);
		when(benutzerGruppeRepository.findByBenutzerAndGruppeId("studentin", "1"))
			.thenReturn(user);
		
		boolean result = gruppeService.checkGroupAccessDenied(account, "1");
		
		assertThat(result).isEqualTo(false);
	}
	
}
