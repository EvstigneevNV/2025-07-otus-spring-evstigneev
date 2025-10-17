package ru.otus.hw.migrations;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentsRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InitDataChangelog {

    private static List<Author> authorsList = List.of(
            new Author(null, "Author_1"),
            new Author(null, "Author_2"),
            new Author(null, "Author_3")
    );

    private static List<Genre> genreList = List.of(
            new Genre(null, "Genre_1"),
            new Genre(null, "Genre_2"),
            new Genre(null, "Genre_3"),
            new Genre(null, "Genre_4"),
            new Genre(null, "Genre_5"),
            new Genre(null, "Genre_6")
    );

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentsRepository commentsRepository;


    private Mono<Void> seed() {
        return authorRepository.deleteAll()
                .then(commentsRepository.deleteAll())
                .then(genreRepository.deleteAll())
                .then(bookRepository.deleteAll())
                .then(Mono.defer(() -> {
                    Mono<List<Author>> authors = authorRepository.saveAll(authorsList).collectList();
                    Mono<List<Genre>> genres = genreRepository.saveAll(genreList).collectList();
                    return Mono.zip(authors, genres)
                            .flatMapMany(t -> bookRepository.saveAll(List.of(
                                    new Book(null, "BookTitle_1", t.getT1().get(0).getId(),
                                            List.of(t.getT2().get(0).getId(), t.getT2().get(1).getId())),
                                    new Book(null, "BookTitle_2", t.getT1().get(1).getId(),
                                            List.of(t.getT2().get(2).getId(), t.getT2().get(3).getId())),
                                    new Book(null, "BookTitle_3", t.getT1().get(2).getId(),
                                            List.of(t.getT2().get(4).getId(), t.getT2().get(5).getId()))
                            )))
                            .then();
                }));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runOnStart() {
        seed().subscribe();
    }
}


