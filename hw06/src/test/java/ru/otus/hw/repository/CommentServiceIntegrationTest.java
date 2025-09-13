package ru.otus.hw.repository;


import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.dto.CommentsDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;


@SpringBootTest
@ActiveProfiles("test")
class CommentServiceIntegrationTest {

    @Autowired EntityManager entityManager;
    @Autowired
    CommentService commentService;

    private Long bookId;
    private Long commentId;

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
        commentId = comment1.getId();
    }

    @Test
    @Transactional
    void findAllByBookIdReturnsDtosWithoutLazyInitialization() {
        List<CommentsDto> list = commentService.findAllByBookId(bookId);
        assertThat(list).extracting(CommentsDto::text).containsExactlyInAnyOrder("c1", "c2");

        assertThatNoException().isThrownBy(() -> list.forEach(CommentsDto::text));
    }

    @Test
    @Transactional
    void findByIdReturnsDtoWithoutLazyInitialization() {
        var dto = commentService.findById(commentId);
        assertThat(dto.text()).isEqualTo("c1");
        assertThatNoException().isThrownBy(dto::text);
    }
}

