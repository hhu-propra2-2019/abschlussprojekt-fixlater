package mops.termine2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.termine2.Konstanten;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UmfragenAbstimmungControllerTest {
	
	@Autowired
	transient MockMvc mvc;
	
	@Test
	@WithMockKeycloackAuth(name = Konstanten.STUDENTIN, roles = Konstanten.STUDENTIN)
	void testUmfragenAbstmmung() throws Exception {
		mvc.perform(get("/termine2/umfragen-abstimmung")).andExpect(status().isOk());
	}
}
