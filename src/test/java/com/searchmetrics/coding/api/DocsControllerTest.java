package com.searchmetrics.coding.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DocsController.class)
class DocsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("can get documentation")
    void getDocs() throws Exception {
        mockMvc.perform(get("/docs"))
                .andExpect(status().isOk());
    }
}