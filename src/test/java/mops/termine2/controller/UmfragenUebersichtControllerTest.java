package mops.termine2.controller;

import mops.termine2.Konstanten;
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
public class UmfragenUebersichtControllerTest {
	
	@Autowired
	transient MockMvc mvc;
	
	@Test
	@WithMockUser(roles = {Konstanten.STUDENTIN})
	void testUmfragen() throws Exception {
		mvc.perform(get("/termine2/umfragen")).andExpect(status().isOk());
	}
}
