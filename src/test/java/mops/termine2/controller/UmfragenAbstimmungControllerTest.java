package mops.termine2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Umfrage;
import mops.termine2.models.UmfrageAntwort;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.KommentarService;
import mops.termine2.services.UmfrageAntwortService;
import mops.termine2.services.UmfrageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UmfragenAbstimmungControllerTest {
	
	Set<String> roleStudentin = new HashSet<>(Arrays.asList(Konstanten.STUDENTIN));
	
	@Autowired
	transient MockMvc mvc;
	
	@MockBean
	UmfrageService umfrageService;
	
	@MockBean
	UmfrageAntwortService umfrageAntwortService;
	
	@MockBean
	GruppeService gruppeService;
	
	@MockBean
	AuthenticationService authenticationService;
	
	@MockBean
	KommentarService kommentarService;
	
	String link = "link";
	
	Account accountStudentin = new Account(Konstanten.STUDENTIN, "email", "Bild", roleStudentin);
	
	// Tests for Get termine2/{link}
	////////////////////////////////////////////////////////////////////////////////7
	
	// ModusLink NochNichtTeilgenommen FristInZukunft
	// Erwarte redirect auf abstimmung
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfrageDetails1() throws Exception {
		Umfrage umfrage = initUmfrage(null, false, true);
		
		when(authenticationService.pruefeEingeloggt(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(umfrageService.loadByLink(link)).thenReturn(umfrage);
		when(umfrageAntwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(false);
		
		mvc.perform(get("/termine2/umfragen/{link}", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/umfragen/" + link + "/abstimmung"));
	}
	
	// ModusLink BereitsTeilgenommen FristInZukunft
	// Erwarte redirect auf ergebnis
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfrageDetails2() throws Exception {
		Umfrage umfrage = initUmfrage(null, true, true);
		
		when(authenticationService.pruefeEingeloggt(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(umfrageService.loadByLink(link)).thenReturn(umfrage);
		when(umfrageAntwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(true);
		
		mvc.perform(get("/termine2/umfragen/{link}", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/umfragen/" + link + "/ergebnis"));
	}
	
	// Umfrage gibt es nicht
	// Erwarte 404
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfrageDetails3() throws Exception {
		Umfrage umfrage = null;
		
		when(authenticationService.pruefeEingeloggt(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(umfrageService.loadByLink(link)).thenReturn(umfrage);
		when(umfrageAntwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(true);
		
		mvc.perform(get("/termine2/umfragen/{link}", link)).andExpect(status().is4xxClientError());
	}
	
	// ModusGruppe (InGruppe) BereitsTeilgenommen FristInZukunft
	// Erwarte redirect auf ergebnis
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfrageDetails4() throws Exception {
		Umfrage umfrage = initUmfrage("1", true, true);
		
		when(authenticationService.pruefeEingeloggt(any(), any())).thenReturn(accountStudentin);
		when(umfrageService.loadByLink(link)).thenReturn(umfrage);
		when(umfrageAntwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(true);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		
		mvc.perform(get("/termine2/umfragen/{link}", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/umfragen/" + link + "/ergebnis"));
	}
	
	// ModusGruppe (NichtInGruppe) BereitsTeilgenommen FristInZukunft
	// Erwarte 403
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfrageDetails5() throws Exception {
		Umfrage umfrage = initUmfrage("1", true, true);
		
		when(authenticationService.pruefeEingeloggt(any(), any())).thenReturn(accountStudentin);
		when(umfrageService.loadByLink(link)).thenReturn(umfrage);
		when(umfrageAntwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(true);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(true);
		
		mvc.perform(get("/termine2/umfragen/{link}", link)).andExpect(status().is4xxClientError());
	}
	
	//tests for get abstimmung
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	// ModusLink NochNichtTeilgenommen FristInZukunft
	// Erwartet isOk
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfrageGet1() throws Exception {
		Umfrage umfrage = initUmfrage(null, false, true);
		
		when(authenticationService.pruefeEingeloggt(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(umfrageService.loadByLink(link)).thenReturn(umfrage);
		when(umfrageAntwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		
		mvc.perform(get("/termine2/umfragen/{link}/abstimmung", link)).andExpect(status().isOk());
	}
	
	// ModusLink NochNichtTeilgenommen FristInVergangenheit
	// Erwartet redirect auf ergebnis
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfrageGet2() throws Exception {
		Umfrage umfrage = initUmfrage(null, false, false);
		
		when(authenticationService.pruefeEingeloggt(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(umfrageService.loadByLink(link)).thenReturn(umfrage);
		when(umfrageAntwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		
		mvc.perform(get("/termine2/umfragen/{link}/abstimmung", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/umfragen/" + link + "/ergebnis"));
	}
	
	// tests for Get ergebnis
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	// ModusLink NochNichtTeilgenommen FristInZukunft
	// Erwartet redirect auf abstimmung
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfrageErgebnisGet1() throws Exception {
		Umfrage umfrage = initUmfrage(null, false, true);
		
		when(authenticationService.pruefeEingeloggt(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(umfrageService.loadByLink(link)).thenReturn(umfrage);
		when(umfrageAntwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(false);
		when(umfrageAntwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		
		mvc.perform(get("/termine2/umfragen/{link}/ergebnis", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/umfragen/" + link + "/abstimmung"));
	}
	
	// ModusGruppe (NichtInGruppe) BereitsAbgestimmt FristInZukunft
	// Erwarte 403
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfrageErgebnisGet2() throws Exception {
		Umfrage umfrage = initUmfrage("1", true, true);
		
		when(authenticationService.pruefeEingeloggt(any(), any())).thenReturn(accountStudentin);
		when(umfrageService.loadByLink(any())).thenReturn(umfrage);
		when(umfrageAntwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(true);
		when(umfrageAntwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		when(umfrageAntwortService.loadAllByLink(any())).thenReturn(initAntworten());
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(true);
		
		mvc.perform(get("/termine2/umfragen/{link}/ergebnis", link)).andExpect(status().is4xxClientError());
	}
	
	// ModusLink BereitsTeilgenommen FristInZukunft
	// Erwartet redirect auf ergebnis
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfrageGet3() throws Exception {
		Umfrage umfrage = initUmfrage(null, true, true);
		
		when(authenticationService.pruefeEingeloggt(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(umfrageService.loadByLink(any())).thenReturn(umfrage);
		when(umfrageAntwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(false);
		when(umfrageAntwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		
		mvc.perform(get("/termine2/umfragen/{link}/ergebnis", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/umfragen/" + link + "/abstimmung"));
	}
	
	// FristInVergangenheit ModusLink BereitsTeilgenommen
	// Erwartet Status OK
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfrageErgebnisGet3() throws Exception {
		Umfrage umfrage = initUmfrage(null, true, false);
		when(authenticationService.pruefeEingeloggt(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(umfrageService.loadByLink(any())).thenReturn(umfrage);
		when(umfrageAntwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(true);
		when(umfrageAntwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		when(umfrageAntwortService.loadAllByLink(any())).thenReturn(initAntworten());
		mvc.perform(get("/termine2/umfragen/{link}/ergebnis", link)).andExpect(status().isOk());
	}
	
	private Umfrage initUmfrage(String gruppeId, Boolean teilgenommen, Boolean fristInZukunft) {
		Umfrage umfrage = new Umfrage();
		umfrage.setLink(link);
		umfrage.setTitel("titel");
		umfrage.setBeschreibung("beschreibung");
		
		List<String> vorschlaege = new ArrayList<String>(Arrays.asList("vorschlag1", "vorschlag2"));
		umfrage.setVorschlaege(vorschlaege);
		
		if (fristInZukunft) {
			umfrage.setFrist(LocalDateTime.now().plusWeeks(1));
		} else {
			umfrage.setFrist(LocalDateTime.now().minusWeeks(1));
		}
		
		umfrage.setGruppeId(gruppeId);
		umfrage.setTeilgenommen(teilgenommen);
		
		return umfrage;
	}
	
	private List<UmfrageAntwort> initAntworten() {
		List<UmfrageAntwort> list = new ArrayList<>();
		list.add(initAntwort());
		return list;
	}
	
	private UmfrageAntwort initAntwort() {
		UmfrageAntwort antwort = new UmfrageAntwort();
		LinkedHashMap<String, Antwort> antwortHashMap = new LinkedHashMap<>();
		antwortHashMap.put("vorschlag1", Antwort.JA);
		antwortHashMap.put("vorschlag2", Antwort.NEIN);
		antwort.setAntworten(antwortHashMap);
		antwort.setPseudonym("pseudonym");
		antwort.setLink(link);
		antwort.setBenutzer(Konstanten.STUDENTIN);
		return antwort;
	}
	
}
