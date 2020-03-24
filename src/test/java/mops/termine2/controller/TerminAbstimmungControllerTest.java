package mops.termine2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TerminAbstimmungControllerTest {
	
	@Autowired
	transient MockMvc mvc;
	/*
	@Test
	@WithMockKeycloackAuth(name = "studentin", roles = "studentin")
	void testTermineAbstimmung() throws Exception {
		mvc.perform(get("/termine2/termine-abstimmung")).andExpect(status().isOk());
	}
	
	 */
}
