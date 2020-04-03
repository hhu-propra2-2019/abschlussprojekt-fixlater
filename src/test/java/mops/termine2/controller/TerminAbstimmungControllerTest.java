package mops.termine2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.enums.Antwort;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.TerminfindungAntwort;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.KommentarService;
import mops.termine2.services.TerminfindungAntwortService;
import mops.termine2.services.TerminfindungService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
public class TerminAbstimmungControllerTest {
	
	Set<String> roleStudentin = new HashSet<>(Arrays.asList(Konstanten.STUDENTIN));
	
	@Autowired
	transient MockMvc mvc;
	
	@MockBean
	TerminfindungService terminService;
	
	@MockBean
	TerminfindungAntwortService antwortService;
	
	@MockBean
	GruppeService gruppeService;
	
	@MockBean
	AuthenticationService authenticationService;
	
	@MockBean
	KommentarService kommentarService;
	
	String link = "link";
	
	Account accountStudentin = new Account(Konstanten.STUDENTIN, "email", "Bild", roleStudentin);
	
	// Tests for Get termine2/{link}
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	// AbstimmungAnpassbar FristInZukunft ErgebnisVorFrist ModusLink  NochNichtTeilgenommen
	//Erwarte redirect auf abstimmung
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineDetails1() throws Exception {
		Terminfindung terminfindung = initTerminfindung(null, false, true, false, true);
		when(authenticationService.checkLoggedIn(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		
		mvc.perform(get("/termine2/{link}", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/" + link + "/abstimmung"));
	}
	
	// AbstimmungAnpassbar FristInZukunft ModusLink BereitsTeilgenommen ErgebnisVorFrist Ture
	//Erwarte redirect auf ergebnis
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineDetails2() throws Exception {
		Terminfindung terminfindung = initTerminfindung(null, false, true, true, true);
		when(authenticationService.checkLoggedIn(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		
		mvc.perform(get("/termine2/{link}", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/" + link + "/ergebnis"));
	}
	
	// Abstimmung gibt es nicht
	// Erwartet 404
	
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineDetails3() throws Exception {
		Terminfindung terminfindung = null;
		when(authenticationService.checkLoggedIn(any(), any())).thenReturn(accountStudentin);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		
		mvc.perform(get("/termine2/{link}", link)).andExpect(status().is4xxClientError());
	}
	// AbstimmungAnpassbar FristInZukunft ModusGruppe BereitsTeilgenommen Zugriff auf Gruppe
	// Erwarte redirect auf ergebnis
	
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineDetails4() throws Exception {
		Terminfindung terminfindung = initTerminfindung("1", false, true, true, true);
		when(authenticationService.checkLoggedIn(any(), any())).thenReturn(accountStudentin);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		
		mvc.perform(get("/termine2/{link}", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/" + link + "/ergebnis"));
	}
	// AbstimmungAnpassbar FristInZukunft ModusGruppe BereitsTeilgenommen  NichtInGruppe
	// Erwarte 403
	
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineDetails5() throws Exception {
		Terminfindung terminfindung = initTerminfindung("1", false, true, true, true);
		when(authenticationService.checkLoggedIn(any(), any())).thenReturn(accountStudentin);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(true);
		
		mvc.perform(get("/termine2/{link}", link)).andExpect(status().is4xxClientError());
	}
	
	//tests for get abstimmung
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	// AbstimmungAnpassbar FristInZukunft ModusLink  NochNichtTeilgenommen
	// Erwartet isOk
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineAbstimmungGet1() throws Exception {
		Terminfindung terminfindung = initTerminfindung(null, false, true, false, true);
		when(authenticationService.checkLoggedIn(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		when(antwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		mvc.perform(get("/termine2/{link}/abstimmung", link)).andExpect(status().isOk());
	}
	
	// AbstimmungAnpassbar FristInVergangenheit ModusLink  NochNichtTeilgenommen
	// Erwartet redirect auf ergebnis
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineAbstimmungGet2() throws Exception {
		Terminfindung terminfindung = initTerminfindung(null, false, true, false, false);
		when(authenticationService.checkLoggedIn(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		when(antwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		mvc.perform(get("/termine2/{link}/abstimmung", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/" + link + "/ergebnis"));
	}
	
	
	// tests for Get ergebnis
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	// AbstimmungAnpassbar FristInZukunft ModusLink  NochNichtTeilgenommen
	// Erwartet redirect auf abstimmung
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineErgebnisGet1() throws Exception {
		Terminfindung terminfindung = initTerminfindung(null, false, true, false, true);
		when(authenticationService.checkLoggedIn(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		when(antwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(false);
		when(antwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		mvc.perform(get("/termine2/{link}/ergebnis", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/" + link + "/abstimmung"));
	}
	
	// AbstimmungAnpassbar FristInZukunft ModusLink  Bereits abgestimmt
	// Erwartet redirect auf abstimmung
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineErgebnisGet2() throws Exception {
		Terminfindung terminfindung = initTerminfindung(null, false, true, true, true);
		when(authenticationService.checkLoggedIn(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		when(antwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(true);
		when(antwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		when(antwortService.loadAllByLink(any())).thenReturn(initAntworten());
		mvc.perform(get("/termine2/{link}/ergebnis", link)).andExpect(status().isOk());
	}
	
	// AbstimmungAnpassbar FristInZukunft ModusGruppe  Bereits abgestimmt Nicht in Gruppe
	// Erwartet redirect auf abstimmung
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineErgebnisGet3() throws Exception {
		Terminfindung terminfindung = initTerminfindung("1", false, true, true, true);
		when(authenticationService.checkLoggedIn(any(), any())).thenReturn(accountStudentin);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		when(antwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(true);
		when(antwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		when(antwortService.loadAllByLink(any())).thenReturn(initAntworten());
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(true);
		
		mvc.perform(get("/termine2/{link}/ergebnis", link)).andExpect(status().is4xxClientError());
	}
	
	// AbstimmungAnpassbar FristInZukunft ModusLink  Teilgenommen ErgebnisVorFrist
	// Erwartet redirect auf ergebnis
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineAbstimmungGet4() throws Exception {
		Terminfindung terminfindung = initTerminfindung(null, false, false, true, true);
		when(authenticationService.checkLoggedIn(any(), any())).thenReturn(accountStudentin);
		when(gruppeService.pruefeGruppenzugriffVerweigert(any(), any())).thenReturn(false);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		when(antwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(false);
		when(antwortService.loadByBenutzerUndLink(any(), any())).thenReturn(initAntwort());
		mvc.perform(get("/termine2/{link}/ergebnis", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/" + link + "/abstimmung"));
	}
	
	// tests post abstimmung/
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	//TODO
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Terminfindung initTerminfindung(
		String gruppeId,
		Boolean einmaligeAbstimmung,
		Boolean ergebnisVorFrist,
		Boolean teilgenommen,
		Boolean fristInZukunft) {
		
		Terminfindung terminfindung = new Terminfindung();
		terminfindung.setLink(link);
		terminfindung.setTitel("Hakuna");
		
		List<LocalDateTime> termine = new ArrayList<LocalDateTime>(
			Arrays.asList(LocalDateTime.of(1, 1, 1, 1, 1)));
		terminfindung.setVorschlaege(termine);
		
		if (fristInZukunft) {
			terminfindung.setFrist(LocalDateTime.now().plusWeeks(1));
		} else {
			terminfindung.setFrist(LocalDateTime.now().minusWeeks(1));
		}
		
		terminfindung.setGruppeId(gruppeId);
		terminfindung.setEinmaligeAbstimmung(einmaligeAbstimmung);
		
		terminfindung.setTeilgenommen(teilgenommen);
		
		terminfindung.setErgebnisVorFrist(ergebnisVorFrist);
		
		return terminfindung;
	}
	
	private List<TerminfindungAntwort> initAntworten() {
		List<TerminfindungAntwort> list = new ArrayList<>();
		list.add(initAntwort());
		return list;
	}
	
	private TerminfindungAntwort initAntwort() {
		TerminfindungAntwort antwort = new TerminfindungAntwort();
		HashMap<LocalDateTime, Antwort> antwortHashMap = new HashMap<>();
		antwortHashMap.put(LocalDateTime.of(1, 1, 1, 1, 1), Antwort.VIELLEICHT);
		antwort.setAntworten(antwortHashMap);
		antwort.setPseudonym("aha");
		antwort.setLink(link);
		antwort.setKuerzel(Konstanten.STUDENTIN);
		
		return antwort;
	}
	
	
}
