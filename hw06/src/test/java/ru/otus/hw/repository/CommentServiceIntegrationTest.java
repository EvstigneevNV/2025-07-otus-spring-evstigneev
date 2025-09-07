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

    @Autowired EntityManager em;
    @Autowired
    CommentService commentService;

    private Long bookId;
    private Long commentId;

    @BeforeEach
    @Transactional
    void setUp() {
        var a = new Author(null, "Author_1");
        var g1 = new Genre(null, "Genre_1");
        var g2 = new Genre(null, "Genre_2");
        var b = new Book(null, "Book_1", a, List.of(g1, g2), null);
        var c1 = new Comment(null, "c1", b);
        var c2 = new Comment(null, "c2", b);
        b.setComments(List.of(c1, c2));


        em.persist(a);
        em.persist(g1);
        em.persist(g2);
        em.persist(b);
        em.persist(c1);
        em.persist(c2);

        bookId = b.getId();
        commentId = c1.getId();
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

