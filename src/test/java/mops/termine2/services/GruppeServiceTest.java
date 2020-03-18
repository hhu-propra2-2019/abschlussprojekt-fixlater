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
	
}
