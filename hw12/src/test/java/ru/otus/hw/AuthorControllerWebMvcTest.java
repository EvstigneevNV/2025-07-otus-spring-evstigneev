package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.configuration.SecurityConfiguration;
import ru.otus.hw.controller.AuthorController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@Import(SecurityConfiguration.class)
class AuthorControllerWebMvcTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @WithMockUser
    void listOk_authenticated() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(
                new AuthorDto(1L, "A1"),
                new AuthorDto(2L, "A2"))
        );

        mvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(authorService).findAll();
    }

    @Test
    void list_redirectsToLogin_whenAnonymous() throws Exception {
        mvc.perform(get("/api/authors"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("/login")));
    }
}
