package ru.otus.hw.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class GenreRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    GenreRepository genreRepository;

    @Test
    @Transactional
    void findAllReturnsPersistedGenres() {
        testEntityManager.persist(new Genre(null, "G1"));
        testEntityManager.persist(new Genre(null, "G2"));
        testEntityManager.flush();
        testEntityManager.clear();

        assertThat(genreRepository.findAll()).extracting(Genre::getName)
                .containsExactlyInAnyOrder("G1", "G2");
    }

    @Test
    @Transactional
    void findAllByIdsReturnsOnlyRequested() {
        var genre1 = testEntityManager.persist(new Genre(null, "G1"));
        var genre2 = testEntityManager.persist(new Genre(null, "G2"));
        var genre3 = testEntityManager.persist(new Genre(null, "G3"));
        testEntityManager.flush();
        testEntityManager.clear();

        var list = genreRepository.findAllByIdIn(Set.of(genre1.getId(), genre3.getId()));
        assertThat(list).extracting(Genre::getName).containsExactlyInAnyOrder("G1","G3");
    }
}
