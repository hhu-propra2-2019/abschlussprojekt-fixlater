package mops.termine2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {
	
	public static final String STUDENTIN = "studentin";
	
	@Autowired
	transient MockMvc mvc;
	
	@Test
	@WithMockUser(roles = {STUDENTIN})
	void testIndex() throws Exception {
		mvc.perform(get("/termine2")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(roles = {STUDENTIN})
	void testTermineAbstimmung() throws Exception {
		mvc.perform(get("/termine2/termine-abstimmung")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(roles = {STUDENTIN})
	void testTerminNeu() throws Exception {
		mvc.perform(get("/termine2/termine-neu")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(roles = {STUDENTIN})
	void testUmfragen() throws Exception {
		mvc.perform(get("/termine2/umfragen")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(roles = {STUDENTIN})
	void testUmfragenAbstmmung() throws Exception {
		mvc.perform(get("/termine2/umfragen-abstimmung")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(roles = {STUDENTIN})
	void testUmfrageNeu() throws Exception {
		mvc.perform(get("/termine2/umfragen-neu")).andExpect(status().isOk());
	}
	
}

