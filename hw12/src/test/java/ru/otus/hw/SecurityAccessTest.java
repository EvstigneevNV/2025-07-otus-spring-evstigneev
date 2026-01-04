package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTest {

    @Autowired
    MockMvc mvc;

    @Test
    void loginPage_isPublic() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void indexHtml_isProtected_forAnonymous() throws Exception {
        mvc.perform(get("/index.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/login")));
    }

    @Test
    void api_isProtected_forAnonymous() throws Exception {
        mvc.perform(get("/api/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/login")));
    }

    @Test
    @WithMockUser
    void api_isAccessible_forAuthenticated() throws Exception {
        mvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }
}