package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.BookController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentsDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerWebMvcTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    private AuthorDto authorDto(long id) {
        return new AuthorDto(id, "Author_" + id);
    }

    private GenreDto genreDto(long id) {
        return new GenreDto(id, "Genre_" + id);
    }

    private CommentsDto commentDto(long id) {
        return new CommentsDto(id, "C" + id);
    }

    private BookDto bookNoComments(long id) {
        return new BookDto(id, "Book_" + id, authorDto(10),
                List.of(genreDto(1), genreDto(2)), List.of());
    }

    private BookDto bookWithComments(long id) {
        return new BookDto(id, "Book_" + id, authorDto(10),
                List.of(genreDto(1), genreDto(3)), List.of(commentDto(100), commentDto(101)));
    }

    @Test
    void indexRendersBooksTable() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(bookNoComments(1), bookNoComments(2)));

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", org.hamcrest.Matchers.hasSize(2)));

        verify(bookService).findAll();
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void bookRendersBookPageWithRefs() throws Exception {
        when(bookService.findById(1L)).thenReturn(bookWithComments(1));
        when(authorService.findAll()).thenReturn(List.of(authorDto(10), authorDto(11)));
        when(genreService.findAll()).thenReturn(List.of(genreDto(1), genreDto(2), genreDto(3)));

        mvc.perform(get("/book/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attributeExists("book", "authors", "genres"));

        verify(bookService).findById(1L);
        verify(authorService).findAll();
        verify(genreService).findAll();
    }

    @Test
    void newBookRendersFormWithRefs() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(authorDto(10)));
        when(genreService.findAll()).thenReturn(List.of(genreDto(1), genreDto(2)));

        mvc.perform(get("/book/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-form"))
                .andExpect(model().attributeExists("authors", "genres"));

        verify(authorService).findAll();
        verify(genreService).findAll();
    }

    @Test
    void createCallsServiceAndRedirectsToBook() throws Exception {
        var created = new BookDto(77L, "Created", authorDto(10), List.of(genreDto(1), genreDto(2)), List.of());
        when(bookService.insert(eq("Created"), eq(10L), any())).thenReturn(created);

        mvc.perform(post("/book")
                        .param("title", "Created")
                        .param("authorId", "10")
                        .param("genreIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/77"))
                .andExpect(flash().attributeExists("msg"));

        ArgumentCaptor<Set<Long>> cap = ArgumentCaptor.forClass(Set.class);
        verify(bookService).insert(eq("Created"), eq(10L), cap.capture());
        assertThat(cap.getValue()).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void updateCallsServiceAndRedirectsSamePage() throws Exception {
        mvc.perform(post("/book/{id}/update", 5)
                        .param("title", "NewTitle")
                        .param("authorId", "10")
                        .param("genreIds", "1", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/5"))
                .andExpect(flash().attributeExists("msg"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<Long>> cap = ArgumentCaptor.forClass(Set.class);
        verify(bookService).update(eq(5L), eq("NewTitle"), eq(10L), cap.capture());
        assertThat(cap.getValue()).containsExactlyInAnyOrder(1L, 3L);
    }

    @Test
    void deleteCallsServiceAndRedirectsHome() throws Exception {
        mvc.perform(post("/book/{id}/delete", 9))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("msg"));

        verify(bookService).deleteById(9L);
    }
}
