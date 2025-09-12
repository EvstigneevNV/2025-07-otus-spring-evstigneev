package ru.otus.hw.repository;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
@ActiveProfiles("test")
class BookServiceIntegrationTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    BookService bookService;

    private Long bookId;

    @BeforeEach
    @Transactional
    void setUp() {
        var author = new Author(null, "Author_1");
        var genre1 = new Genre(null, "Genre_1");
        var genre2 = new Genre(null, "Genre_2");
        var book = new Book(null, "Book_1", author, List.of(genre1, genre2));
        var comment1 = new Comment(null, "c1", book);
        var comment2 = new Comment(null, "c2", book);
        book.setComments(List.of(comment1, comment2));


        entityManager.persist(author);
        entityManager.persist(genre1);
        entityManager.persist(genre2);
        entityManager.persist(book);
        entityManager.persist(comment1);
        entityManager.persist(comment2);

        bookId = book.getId();
    }

    @Test
    @Transactional
    void findAllExposesAuthorAndGenresWithoutLazyInitialization() {
        List<BookDto> list = bookService.findAll();
        assertThat(list).hasSize(1);

        BookDto dto = list.get(0);

        assertThat(dto.author().fullName()).isEqualTo("Author_1");
        assertThat(dto.genres()).extracting(GenreDto::name)
                .containsExactlyInAnyOrder("Genre_1", "Genre_2");

        assertThatCode(() -> {
            dto.author().fullName();
            dto.genres().forEach(GenreDto::name);
        }).doesNotThrowAnyException();
    }

    @Test
    @Transactional
    void findByIdExposesAuthorWithoutLazyInitialization() {
        var dto = bookService.findById(bookId);
        assertThat(dto.author().fullName()).isEqualTo("Author_1");

        assertThatNoException().isThrownBy(() -> dto.author().fullName());
    }
}