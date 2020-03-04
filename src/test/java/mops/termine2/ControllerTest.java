package mops.termine2;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest
public class ControllerTest {


    @Autowired
    MockMvc mvc;

    @Test
    void testIndex() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }


}
