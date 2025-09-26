package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.BookService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void insertFindUpdateDelete() {
        var a = authorRepository.save(new Author(null, "Orwell"));
        var g1 = genreRepository.save(new Genre(null, "dystopia"));
        var g2 = genreRepository.save(new Genre(null, "classic"));

        var created = bookService.insert("1984", a.getId(), Set.of(g1.getId(), g2.getId()));
        assertThat(created.id()).isNotBlank();
        assertThat(created.title()).isEqualTo("1984");

        assertThat(bookService.findAll()).hasSize(1);

        var loaded = bookService.findById(created.id());
        assertThat(loaded.title()).isEqualTo("1984");

        var updated = bookService.update(created.id(), "Nineteen Eighty-Four", a.getId(), Set.of(g1.getId()));
        assertThat(updated.title()).isEqualTo("Nineteen Eighty-Four");
        assertThat(updated.genres().size()).isEqualTo(1);

        bookService.deleteById(created.id());
        assertThat(bookRepository.findAll()).isEmpty();
    }
}
