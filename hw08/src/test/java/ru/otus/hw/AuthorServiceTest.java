package ru.otus.hw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthorServiceTest extends BaseMongoTest {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("findAll возвращает всех авторов в DTO")
    void findAll() {
        authorRepository.saveAll(List.of(
                new Author(null, "Orwell"),
                new Author(null, "Bradbury")
        ));

        var dtos = authorService.findAll();
        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting("fullName").contains("Orwell", "Bradbury");
    }
}
