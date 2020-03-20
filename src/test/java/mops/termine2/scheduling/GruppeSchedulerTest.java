package mops.termine2.scheduling;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import mops.termine2.database.BenutzerGruppeRepository;
import mops.termine2.database.entities.BenutzerGruppeDB;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class GruppeSchedulerTest {
	
	private GruppeScheduler scheduler;
	
	private transient RestTemplate rt;
	
	private transient BenutzerGruppeRepository BGrepo;
	
	private transient MockRestServiceServer server;
	
	private static String groupNew1 = "{ \"groupList\": [ { \"description\": \"string\", \"id\": 0, \"members\": [ { \"email\":"
		+ "\"string1\", \"familyname\": \"string1\", \"givenname\": \"string1\", \"user_id\": \"string1\""
		+ "} ], \"parent\": 0, \"roles\": { \"additionalProp1\": \"string\", \"additionalProp2\":"
		+ "\"string\", \"additionalProp3\": \"string\" }, \"title\": \"string\", \"type\": \"SIMPLE\","
		+ "\"visibility\": \"PUBLIC\" } ], \"status\": 1 }";
	
	private static String groupNew2 = "{ \"groupList\": [ { \"description\": \"string\", \"id\": 0, \"members\": [ { \"email\":"
		+ "\"string1\", \"familyname\": \"string1\", \"givenname\": \"string1\", \"user_id\": \"string1\""
		+ "}, { \"email\":\"string3\", \"familyname\": \"string3\", \"givenname\": \"string3\", \"user_id\": \"string3\""
		+ "} ], \"parent\": 0, \"roles\": { \"additionalProp1\": \"string\", \"additionalProp2\":"
		+ "\"string\", \"additionalProp3\": \"string\" }, \"title\": \"string\", \"type\": \"SIMPLE\","
		+ "\"visibility\": \"PUBLIC\" } ], \"status\": 1 }";
	
	private static String groupDelete = "{ \"groupList\": [ { \"description\": \"null\", \"id\": 0, \"members\": [], \"parent\": 0, \"roles\": {}, "
		+ "\"title\": \"null\", \"type\": \"SIMPLE\","
		+ "\"visibility\": \"PUBLIC\" } ], \"status\": 1 }";
	
	@BeforeEach
	public void setUp() {
		rt = new RestTemplate();
		BGrepo = mock(BenutzerGruppeRepository.class);
		server = MockRestServiceServer.bindTo(rt).build();
		scheduler = new GruppeScheduler(BGrepo, rt);
	}
	
	@Test
	public void testStatus0() {
		when(BGrepo.findBenutzerByGruppeId(0L)).thenReturn(new ArrayList<String>());
		
		server.expect(ExpectedCount.once(),
			requestTo("http://localhost:8082/gruppen2/api/updateGroups/0"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(groupNew1, MediaType.APPLICATION_JSON));
		
		scheduler.updateGruppe();
		server.verify();
	}
	
	@Test
	public void fuegeNutzerHinzu() {
		when(BGrepo.findBenutzerByGruppeId(0L)).thenReturn(new ArrayList<String>());
		BenutzerGruppeDB erwartet = new BenutzerGruppeDB();
		erwartet.setBenutzer("string1");
		erwartet.setGruppe("string");
		erwartet.setGruppeId(0L);
		
		server.expect(ExpectedCount.once(),
			requestTo("http://localhost:8082/gruppen2/api/updateGroups/0"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(groupNew1, MediaType.APPLICATION_JSON));
		
		scheduler.updateGruppe();
		
		verify(BGrepo, times(1)).save(erwartet);
	}
	
	@Test
	public void loescheNutzer() {
		when(BGrepo.findBenutzerByGruppeId(0L)).thenReturn(new ArrayList<String>(Arrays.asList("string1", "string2")));
		BenutzerGruppeDB erwartet = new BenutzerGruppeDB();
		erwartet.setBenutzer("string2");
		erwartet.setGruppe("string");
		erwartet.setGruppeId(0L);
		
		server.expect(ExpectedCount.once(),
			requestTo("http://localhost:8082/gruppen2/api/updateGroups/0"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(groupNew1, MediaType.APPLICATION_JSON));
		
		scheduler.updateGruppe();
		
		verify(BGrepo, times(1)).deleteByBenutzerAndGruppeId("string2", 0L);
		verify(BGrepo, never()).save(any());
	}
	
	@Test
	public void loescheGruppe() {
		server.expect(ExpectedCount.once(),
			requestTo("http://localhost:8082/gruppen2/api/updateGroups/0"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(groupDelete, MediaType.APPLICATION_JSON));
		
		scheduler.updateGruppe();
		
		verify(BGrepo, times(1)).deleteAllByGruppeId(0L);
	}
	
	@Test
	public void loescheUndFuegeNutzerHinzu() {
		when(BGrepo.findBenutzerByGruppeId(0L)).thenReturn(new ArrayList<String>(Arrays.asList("string1", "string2")));
		BenutzerGruppeDB erwartetNeu = new BenutzerGruppeDB();
		erwartetNeu.setBenutzer("string3");
		erwartetNeu.setGruppe("string");
		erwartetNeu.setGruppeId(0L);
		
		server.expect(ExpectedCount.once(),
			requestTo("http://localhost:8082/gruppen2/api/updateGroups/0"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(groupNew2, MediaType.APPLICATION_JSON));
		
		scheduler.updateGruppe();
		
		verify(BGrepo, times(1)).deleteByBenutzerAndGruppeId("string2", 0L);
		verify(BGrepo, times(1)).save(erwartetNeu);
	}
	
}
