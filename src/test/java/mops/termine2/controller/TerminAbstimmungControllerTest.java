package mops.termine2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.termine2.Konstanten;
import mops.termine2.authentication.Account;
import mops.termine2.models.Terminfindung;
import mops.termine2.services.AuthenticationService;
import mops.termine2.services.GruppeService;
import mops.termine2.services.KommentarService;
import mops.termine2.services.TerminAntwortService;
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
	TerminAntwortService antwortService;
	
	@MockBean
	GruppeService gruppeService;
	
	@MockBean
	AuthenticationService authenticationService;
	
	@MockBean
	KommentarService kommentarService;
	
	String link = "link";
	
	Account accountStudentin = new Account(Konstanten.STUDENTIN, "email", "Bild", roleStudentin);
	
	// AbstimmungAnpassbar FristInZukunft ModusLink  NochNichtTeilgenommen
	//Erwarte redirect auf abstimmung
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineDetails1() throws Exception {
		Terminfindung terminfindung = init1(null, false, false, true);
		when(authenticationService.createAccountFromPrincipal(any())).thenReturn(accountStudentin);
		when(gruppeService.loadByBenutzer(accountStudentin)).thenReturn(null);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		when(antwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(false);
		
		mvc.perform(get("/termine2/{link}", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/" + link + "/abstimmung"));
	}
	
	// AbstimmungAnpassbar FristInZukunft ModusLink BereitsTeilgenommen
	//Erwarte redirect auf ergebnis
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testTermineDetails2() throws Exception {
		Terminfindung terminfindung = init1(null, false, true, true);
		when(authenticationService.createAccountFromPrincipal(any())).thenReturn(accountStudentin);
		when(gruppeService.loadByBenutzer(accountStudentin)).thenReturn(null);
		when(terminService.loadByLinkMitTerminenForBenutzer(any(), any())).thenReturn(terminfindung);
		when(antwortService.hatNutzerAbgestimmt(any(), any())).thenReturn(true);
		
		mvc.perform(get("/termine2/{link}", link)).andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/termine2/" + link + "/ergebnis"));
	}
	
	
	private Terminfindung init1(
		Long gruppeId, Boolean einmaligeAbstimmung, Boolean teilgenommen, Boolean fristInZukunft) {
		
		Terminfindung ret = new Terminfindung();
		ret.setLink(link);
		ret.setTitel("Hakuna");
		
		List<LocalDateTime> termine = new ArrayList(Arrays.asList(LocalDateTime.now()));
		ret.setVorschlaege(termine);
		
		LocalDateTime frist;
		if (fristInZukunft) {
			frist = LocalDateTime.now().plusWeeks(1);
		} else {
			frist = LocalDateTime.now().minusWeeks(1);
		}
		ret.setFrist(frist);
		
		ret.setGruppeId(gruppeId);
		ret.setEinmaligeAbstimmung(einmaligeAbstimmung);
		
		ret.setTeilgenommen(teilgenommen);
		
		return ret;
	}
	
}
