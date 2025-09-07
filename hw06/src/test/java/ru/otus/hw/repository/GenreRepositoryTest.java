package ru.otus.hw.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaGenreRepository.class)
@ActiveProfiles("test")
class GenreRepositoryTest {

    @Autowired
    TestEntityManager em;
    @Autowired
    GenreRepository repo;

    @Test
    @Transactional
    void findAllReturnsPersistedGenres() {
        em.persist(new Genre(null, "G1"));
        em.persist(new Genre(null, "G2"));
        em.flush();
        em.clear();

        assertThat(repo.findAll()).extracting(Genre::getName)
                .containsExactlyInAnyOrder("G1", "G2");
    }

    @Test
    @Transactional
    void findAllByIdsReturnsOnlyRequested() {
        var g1 = em.persist(new Genre(null, "G1"));
        var g2 = em.persist(new Genre(null, "G2"));
        var g3 = em.persist(new Genre(null, "G3"));
        em.flush();
        em.clear();

        var list = repo.findAllByIds(Set.of(g1.getId(), g3.getId()));
        assertThat(list).extracting(Genre::getName).containsExactlyInAnyOrder("G1","G3");
    }
}
