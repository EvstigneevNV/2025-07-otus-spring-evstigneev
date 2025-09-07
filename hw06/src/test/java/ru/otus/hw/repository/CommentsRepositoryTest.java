package ru.otus.hw.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.CommentsRepository;
import ru.otus.hw.repositories.JpaCommentsRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaCommentsRepository.class)
@ActiveProfiles("test")
class CommentsRepositoryTest {

    @Autowired
    TestEntityManager em;
    @Autowired
    CommentsRepository repo;

    @Test
    void findAllByBookId_returnsComments() {
        var a = em.persist(new Author(null, "A"));
        var b = em.persist(new Book(null, "T", a, null, null));
        em.persist(new Comment(null, "c1", b));
        em.persist(new Comment(null, "c2", b));
        em.flush();
        em.clear();

        var list = repo.findAllByBookId(b.getId());
        assertThat(list).extracting(Comment::getText).containsExactlyInAnyOrder("c1","c2");
    }

    @Test
    void save_and_delete() {
        var a = em.persist(new Author(null, "A"));
        var b = em.persist(new Book(null, "T", a, null, null));
        var c = repo.save(new Comment(null, "c", b));

        assertThat(c.getId()).isNotNull();

        repo.deleteById(c.getId());
        em.flush();
        em.clear();

        assertThat(em.find(Comment.class, c.getId())).isNull();
    }
}
