package ru.otus.hw;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.CommentController;
import ru.otus.hw.dto.CommentsDto;
import ru.otus.hw.services.CommentService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerWebMvcTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private CommentService commentService;

    @Test
    void create201() throws Exception {
        var req = new CommentController.UpsertComment("txt");
        when(commentService.insert("txt", 5L)).thenReturn(new CommentsDto(100L,"txt"));

        mvc.perform(post("/api/books/{bookId}/comments",5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(100)));
    }

    @Test
    void update200() throws Exception {
        var req = new CommentController.UpsertComment("upd");
        when(commentService.update(100L,"upd",5L)).thenReturn(new CommentsDto(100L,"upd"));

        mvc.perform(put("/api/books/{bookId}/comments/{id}",5,100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is("upd")));
    }

    @Test
    void delete204() throws Exception {
        mvc.perform(delete("/api/books/{bookId}/comments/{id}",5,100))
                .andExpect(status().isNoContent());
        verify(commentService).deleteById(eq(100L));
    }
}
