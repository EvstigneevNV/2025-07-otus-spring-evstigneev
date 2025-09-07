package ru.otus.hw.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.JpaAuthorRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuthorRepository.class)
@ActiveProfiles("test")
class AuthorRepositoryTest {

    @Autowired
    TestEntityManager em;
    @Autowired
    AuthorRepository repo;

    @Test
    @Transactional
    void findAllReturnsPersistedAuthors() {
        var a1 = em.persist(new Author(null, "A1"));
        var a2 = em.persist(new Author(null, "A2"));
        em.flush();
        em.clear();

        List<Author> all = repo.findAll();

        assertThat(all).extracting(Author::getFullName).containsExactlyInAnyOrder("A1", "A2");
    }

    @Test
    @Transactional
    void findByIdReturnsAuthor() {
        var a = em.persist(new Author(null, "A"));
        em.flush(); em.clear();

        assertThat(repo.findById(a.getId())).isPresent()
                .get().extracting(Author::getFullName).isEqualTo("A");
    }
}
