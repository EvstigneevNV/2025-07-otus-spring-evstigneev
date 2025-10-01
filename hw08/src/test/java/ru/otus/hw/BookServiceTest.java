package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.BookService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class BookServiceTest extends BaseMongoTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;


    private Author a;
    private Genre g1;
    private Genre g2;

    @BeforeEach
    void seed() {
        a  = authorRepository.save(new Author(null, "Orwell"));
        g1 = genreRepository.save(new Genre(null, "dystopia"));
        g2 = genreRepository.save(new Genre(null, "classic"));
    }

    @Test
    void insertCreatesBook() {
        var created = bookService.insert("1984", a.getId(), Set.of(g1.getId(), g2.getId()));
        assertThat(created.id()).isNotBlank();
        assertThat(bookRepository.findAll()).hasSize(1);
    }

    @Test
    void findAllReturnsAll() {
        bookService.insert("1984", a.getId(), Set.of(g1.getId()));
        assertThat(bookService.findAll()).hasSize(1);
    }

    @Test
    void findByIdReturnsOne() {
        var created = bookService.insert("1984", a.getId(), Set.of(g1.getId()));
        var loaded = bookService.findById(created.id());
        assertThat(loaded.title()).isEqualTo("1984");
    }

    @Test
    void findByIdThrowsIfNotFound() {
        assertThatThrownBy(() -> bookService.findById("missing"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateChangesFields() {
        var created = bookService.insert("1984", a.getId(), Set.of(g1.getId(), g2.getId()));
        var updated = bookService.update(created.id(), "Nineteen Eighty-Four", a.getId(), Set.of(g1.getId()));
        assertThat(updated.title()).isEqualTo("Nineteen Eighty-Four");
        assertThat(updated.genres()).hasSize(1);
    }

    @Test
    void deleteByIdRemovesBook() {
        var created = bookService.insert("1984", a.getId(), Set.of(g1.getId()));
        bookService.deleteById(created.id());
        assertThat(bookRepository.findAll()).isEmpty();
    }

    @Test
    void insertThrowsIfGenresEmpty() {
        assertThatThrownBy(() -> bookService.insert("x", a.getId(), Set.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insertThrowsIfAuthorMissing() {
        assertThatThrownBy(() -> bookService.insert("x", "no-author", Set.of(g1.getId())))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
