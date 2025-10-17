package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentsRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentsRepository commentsRepository;


    @Override
    @Transactional(readOnly = true)
    public Mono<BookDto> findById(String id) {
        var book = bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s not found".formatted(id))));

        var author = book.flatMap(i -> authorRepository.findById(i.getAuthorId()));

        var genres = book.flatMap(i -> genreRepository.findAllByIdIn(i.getGenreIds()).collectList());

        var comments = commentsRepository.findAllByBookId(id).collectList();

        return Mono.zip(book, author, genres, comments)
                .map(i ->
                        BookDto.bookToBookDto(
                                i.getT1(),
                                i.getT2(),
                                i.getT3(),
                                i.getT4()
                        ));

    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BookDto> findAll() {
        return bookRepository.findAll().collectList()
                .flatMapMany(books -> {
                    var authorIds = books.stream().map(Book::getAuthorId).collect(Collectors.toList());
                    var genreIds = books.stream().flatMap(b -> b.getGenreIds().stream()).collect(Collectors.toList());
                    Mono<Map<String, Author>> authorsById =
                            authorRepository.findByIdIn(authorIds).collectMap(Author::getId, a -> a);
                    Mono<Map<String, Genre>> genresById =
                            genreRepository.findAllByIdIn(genreIds).collectMap(Genre::getId, g -> g);
                    return Mono.zip(authorsById, genresById)
                            .flatMapMany(t -> {
                                var aMap = t.getT1();
                                var gMap = t.getT2();
                                return Flux.fromIterable(books)
                                        .map(b -> {
                                            var author = aMap.get(b.getAuthorId());
                                            var genres = b.getGenreIds().stream()
                                                    .map(gMap::get)
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toList());
                                            return BookDto.bookToBookDto(b, author, genres, List.of());
                                        });
                            });
                });
    }

    @Override
    @Transactional
    public Mono<BookDto> insert(String title, String authorId, List<String> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public Mono<BookDto> update(String id, String title, String authorId, List<String> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(String id) {

        return commentsRepository.deleteAllByBookId(id)
                .then(bookRepository.deleteById(id));
    }

    private Mono<BookDto> save(String id, String title, String authorId, List<String> genresIds) {
        if (genresIds == null || genresIds.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Genres ids must not be null"));
        }
        var authorMono = authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Author with id %s not found".formatted(authorId))));
        var genresMono = genreRepository.findAllByIdIn(genresIds)
                .collectList()
                .flatMap(list -> {
                    if (list.isEmpty() || list.size() != genresIds.size()) {
                        return Mono.error(new EntityNotFoundException(
                                "One or all genres with ids %s not found".formatted(genresIds)));
                    }
                    return Mono.just(list);
                });
        return Mono.zip(
                        Mono.zip(authorMono, genresMono)
                                .map(t -> new Book(id, title, authorId, genresIds))
                                .flatMap(bookRepository::save),
                        authorMono,
                        genresMono)
                .map(x ->
                        BookDto.bookToBookDto(x.getT1(), x.getT2(), x.getT3(), List.of()));
    }
}
