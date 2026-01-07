package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;

@Component
@RequiredArgsConstructor
public class LibraryHealthIndicator implements HealthIndicator {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    @Override
    public Health health() {
        try {
            long authors = authorRepository.count();
            long genres = genreRepository.count();

            Health.Builder builder = (authors > 0 && genres > 0) ? Health.up() : Health.down();

            return builder
                    .withDetail("authors", authors)
                    .withDetail("genres", genres)
                    .withDetail("message", "Library reference data check")
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("message", "Library health check failed")
                    .build();
        }
    }
}
