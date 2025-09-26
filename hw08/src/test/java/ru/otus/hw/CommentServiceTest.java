package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void insertFindAllByBookDeleteAllByBook() {
        var a = authorRepository.save(new Author(null, "Orwell"));
        var g = genreRepository.save(new Genre(null, "dystopia"));
        var book = bookRepository.save(new Book(null, "1984", a, List.of(g)));

        var c1 = commentService.insert("good", book.getId());
        var c2 = commentService.insert("must read", book.getId());
        assertThat(c1.id()).isNotBlank();
        assertThat(c2.id()).isNotBlank();

        var list = commentService.findAllByBookId(book.getId());
        assertThat(list).hasSize(2);

        commentService.deleteAllByBookId(book.getId());
        assertThat(commentService.findAllByBookId(book.getId())).isEmpty();
    }
}
