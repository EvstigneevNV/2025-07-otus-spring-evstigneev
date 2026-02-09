package ru.otus.hw.actuator;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

@Component
@RequiredArgsConstructor
public class LibraryMetricsBinder implements MeterBinder {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    public void bindTo(MeterRegistry registry) {
        Tags tags = Tags.of("app", "hw16");

        Gauge.builder("library.authors.count", authorRepository, r -> (double) r.count())
                .description("Number of authors in library")
                .tags(tags)
                .register(registry);

        Gauge.builder("library.genres.count", genreRepository, r -> (double) r.count())
                .description("Number of genres in library")
                .tags(tags)
                .register(registry);

        Gauge.builder("library.books.count", bookRepository, r -> (double) r.count())
                .description("Number of books in library")
                .tags(tags)
                .register(registry);
    }
}
