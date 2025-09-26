package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.GenreService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GenreServiceTest extends BaseMongoTest {

    @Autowired
    private GenreService genreService;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void findAll() {
        genreRepository.save(new Genre(null, "sci-fi"));
        genreRepository.save(new Genre(null, "dystopia"));

        var dtos = genreService.findAll();
        assertThat(dtos).extracting("name").containsExactlyInAnyOrder("sci-fi", "dystopia");

        var ids = genreRepository.findAll().stream().map(Genre::getId).collect(java.util.stream.Collectors.toSet());
        var found = genreRepository.findAllByIdIn(ids);
        assertThat(found).hasSize(2);
    }
}
