package ru.otus.hw;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.BookController;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentsDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.request.UpsertBook;
import ru.otus.hw.services.BookService;

@WebMvcTest(BookController.class)
class BookControllerWebMvcTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private BookService bookService;

    private AuthorDto a(long id) {
        return new AuthorDto(id, "Author_" + id);
    }

    private GenreDto g(long id) {
        return new GenreDto(id, "Genre_" + id);
    }

    private CommentsDto c(long id) {
        return new CommentsDto(id, "C" + id);
    }

    @Test
    void listOk() throws Exception {
        var b1 = new BookDto(1L, "B1", a(10), List.of(g(1)), List.of());
        var b2 = new BookDto(2L, "B2", a(11), List.of(g(2)), List.of());
        when(bookService.findAll()).thenReturn(List.of(b1, b2));

        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("B1")));
    }

    @Test
    void getOk() throws Exception {
        var b = new BookDto(7L, "B", a(10), List.of(g(1)), List.of(c(100)));
        when(bookService.findById(7L)).thenReturn(b);

        mvc.perform(get("/api/books/{id}", 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.comments", hasSize(1)));
    }

    @Test
    void create201() throws Exception {
        var req = new UpsertBook("Created", 10L, List.of(1L, 2L));
        var created = new BookDto(77L, "Created", a(10), List.of(g(1), g(2)), List.of());
        when(bookService.insert(eq("Created"), eq(10L), any())).thenReturn(created);

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(77)));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<Long>> cap = ArgumentCaptor.forClass(Set.class);
        verify(bookService).insert(eq("Created"), eq(10L), cap.capture());
        assertThat(cap.getValue()).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void update200() throws Exception {
        var req = new UpsertBook("New", 11L, List.of(3L));
        var upd = new BookDto(5L, "New", a(11), List.of(g(3)), List.of());
        when(bookService.update(eq(5L), eq("New"), eq(11L), any())).thenReturn(upd);

        mvc.perform(put("/api/books/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("New")));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<Long>> cap = ArgumentCaptor.forClass(Set.class);
        verify(bookService).update(eq(5L), eq("New"), eq(11L), cap.capture());
        assertThat(cap.getValue()).containsExactly(3L);
    }

    @Test
    void delete204() throws Exception {
        mvc.perform(delete("/api/books/{id}", 9))
                .andExpect(status().isNoContent());
        verify(bookService).deleteById(9L);
    }
}

