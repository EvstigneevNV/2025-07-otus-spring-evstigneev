package ru.otus.hw.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AuthorRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    AuthorRepository authorRepository;

    @Test
    @Transactional
    void findAllReturnsPersistedAuthors() {
        var author1 = testEntityManager.persist(new Author(null, "A1"));
        var author2 = testEntityManager.persist(new Author(null, "A2"));
        testEntityManager.flush();
        testEntityManager.clear();

        List<Author> all = authorRepository.findAll();

        assertThat(all).extracting(Author::getFullName).containsExactlyInAnyOrder("A1", "A2");
    }

    @Test
    @Transactional
    void findByIdReturnsAuthor() {
        var a = testEntityManager.persist(new Author(null, "A"));
        testEntityManager.flush();
        testEntityManager.clear();

        assertThat(authorRepository.findById(a.getId())).isPresent()
                .get().extracting(Author::getFullName).isEqualTo("A");
    }
}
