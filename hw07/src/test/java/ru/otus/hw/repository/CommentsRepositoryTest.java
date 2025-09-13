package ru.otus.hw.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.CommentsRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CommentsRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    CommentsRepository commentsRepository;

    @Test
    void findAllByBookIdReturnsComments() {
        var author = testEntityManager.persist(new Author(null, "A"));
        var book = testEntityManager.persist(new Book(null, "T", author, null));
        testEntityManager.persist(new Comment(null, "c1", book));
        testEntityManager.persist(new Comment(null, "c2", book));
        testEntityManager.flush();
        testEntityManager.clear();

        var list = commentsRepository.findAllByBookId(book.getId());
        assertThat(list).extracting(Comment::getText).containsExactlyInAnyOrder("c1","c2");
    }

    @Test
    void saveAndDelete() {
        var author = testEntityManager.persist(new Author(null, "A"));
        var book = testEntityManager.persist(new Book(null, "T", author, null));
        var comment = commentsRepository.save(new Comment(null, "c", book));

        assertThat(comment.getId()).isNotNull();

        commentsRepository.deleteById(comment.getId());
        testEntityManager.flush();
        testEntityManager.clear();

        assertThat(testEntityManager.find(Comment.class, comment.getId())).isNull();
    }
}
