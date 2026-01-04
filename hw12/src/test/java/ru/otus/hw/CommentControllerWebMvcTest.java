package ru.otus.hw;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.configuration.SecurityConfiguration;
import ru.otus.hw.controller.CommentController;
import ru.otus.hw.dto.CommentsDto;
import ru.otus.hw.services.CommentService;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(SecurityConfiguration.class)
class CommentControllerWebMvcTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private CommentService commentService;

    @Test
    @WithMockUser
    void create201_authenticated() throws Exception {
        var req = new CommentController.UpsertComment("txt");
        when(commentService.insert("txt", 5L)).thenReturn(new CommentsDto(100L, "txt"));

        mvc.perform(post("/api/books/{bookId}/comments", 5)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(100)));
    }

    @Test
    @WithMockUser
    void update200_authenticated() throws Exception {
        var req = new CommentController.UpsertComment("upd");
        when(commentService.update(100L, "upd", 5L)).thenReturn(new CommentsDto(100L, "upd"));

        mvc.perform(put("/api/books/{bookId}/comments/{id}", 5, 100)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is("upd")));
    }

    @Test
    @WithMockUser
    void delete204_authenticated() throws Exception {
        mvc.perform(delete("/api/books/{bookId}/comments/{id}", 5, 100).with(csrf()))
                .andExpect(status().isNoContent());
        verify(commentService).deleteById(eq(100L));
    }

    @Test
    void create_redirectsToLogin_whenAnonymous() throws Exception {
        var req = new CommentController.UpsertComment("x");

        mvc.perform(post("/api/books/{bookId}/comments", 5)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("/login")));
    }
}
