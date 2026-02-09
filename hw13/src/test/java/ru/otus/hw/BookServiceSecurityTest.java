package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class BookServiceSecurityTest {

    @Autowired
    private BookService bookService;

    @MockitoBean
    private AuthorRepository authorRepository;

    @MockitoBean
    private GenreRepository genreRepository;

    @MockitoBean
    private BookRepository bookRepository;

    @Test
    @WithMockUser(roles = "USER")
    void userCannotInsert() {
        assertThatThrownBy(() -> bookService.insert("T", 1L, Set.of(1L)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotUpdateOrDelete() {
        assertThatThrownBy(() -> bookService.update(1L, "T", 1L, Set.of(1L)))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> bookService.deleteById(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanInsert() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(new Author(1L, "A")));
        when(genreRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of(new Genre(1L, "G")));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId(10L);
            return b;
        });

        var dto = bookService.insert("T", 1L, Set.of(1L));
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.title()).isEqualTo("T");
    }
}
