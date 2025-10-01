package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CommentServiceTest extends BaseMongoTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Book book;

    @BeforeEach
    void seed() {
        var a = authorRepository.save(new Author(null, "Orwell"));
        var g = genreRepository.save(new Genre(null, "dystopia"));
        book = bookRepository.save(new Book(null, "1984", a, List.of(g)));
    }

    @Test
    void insert_createsComment() {
        var c = commentService.insert("good", book.getId());
        assertThat(c.id()).isNotBlank();
    }

    @Test
    void findAllByBookId_returnsList() {
        commentService.insert("good", book.getId());
        commentService.insert("must read", book.getId());
        var list = commentService.findAllByBookId(book.getId());
        assertThat(list).hasSize(2);
    }

    @Test
    void findById_returnsOne() {
        var c = commentService.insert("good", book.getId());
        var loaded = commentService.findById(c.id());
        assertThat(loaded.text()).isEqualTo("good");
    }

    @Test
    void findById_throwsIfMissing() {
        assertThatThrownBy(() -> commentService.findById("missing"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_changesTextAndBook() {
        var c = commentService.insert("good", book.getId());
        var updated = commentService.update(c.id(), "better", book.getId());
        assertThat(updated.text()).isEqualTo("better");
    }

    @Test
    void deleteById_removesOne() {
        var c = commentService.insert("good", book.getId());
        commentService.deleteById(c.id());
        assertThat(commentService.findAllByBookId(book.getId())).isEmpty();
    }

    @Test
    void deleteAllByBookId_removesAll() {
        commentService.insert("a", book.getId());
        commentService.insert("b", book.getId());
        commentService.deleteAllByBookId(book.getId());
        assertThat(commentService.findAllByBookId(book.getId())).isEmpty();
    }

    @Test
    void insert_throwsIfBookMissing() {
        assertThatThrownBy(() -> commentService.insert("x", "no-book"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_throwsIfBookMissing() {
        var c = commentService.insert("x", book.getId());
        assertThatThrownBy(() -> commentService.update(c.id(), "y", "no-book"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
