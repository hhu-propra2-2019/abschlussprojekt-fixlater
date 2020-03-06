package mops.termine2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ControllerTest {
	
	@Autowired
	transient MockMvc mvc;
	
	@Test
	void testIndex() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk());
	}
	
	@Test
	void testTermineAbstimmung() throws Exception {
		mvc.perform(get("/termine-abstimmung")).andExpect(status().isOk());
	}
	
	@Test
	void testTerminNeu() throws Exception {
		mvc.perform(get("/termin-neu")).andExpect(status().isOk());
	}
	
	@Test
	void testUmfragenAbstmmung() throws Exception {
		mvc.perform(get("/umfragen-abstimmung")).andExpect(status().isOk());
	}
	
	@Test
	void testUmfrageNeu() throws Exception {
		mvc.perform(get("/umfrage-neu")).andExpect(status().isOk());
	}
	
}
