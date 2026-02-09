package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.DbUnavailableException;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;


    @Override
    @CircuitBreaker(name = "db", fallbackMethod = "findByIdFallback")
    @RateLimiter(name = "db")
    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        return bookRepository.findById(id).map(BookDto::bookToBookDto)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
    }

    @Override
    @CircuitBreaker(name = "db", fallbackMethod = "findAllFallback")
    @RateLimiter(name = "db")
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(BookDto::bookToBookDto).toList();
    }

    @Override
    @CircuitBreaker(name = "db", fallbackMethod = "insertFallback")
    @RateLimiter(name = "db")
    @Transactional
    public BookDto insert(String title, Long authorId, Set<Long> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Override
    @CircuitBreaker(name = "db", fallbackMethod = "updateFallback")
    @RateLimiter(name = "db")
    @Transactional
    public BookDto update(Long id, String title, Long authorId, Set<Long> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Override
    @CircuitBreaker(name = "db", fallbackMethod = "deleteByIdFallback")
    @RateLimiter(name = "db")
    @Transactional
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    private BookDto save(Long id, String title, Long authorId, Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findAllByIdIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new Book(id, title, author, genres);
        return BookDto.bookToBookDto(bookRepository.save(book));
    }


    private BookDto findByIdFallback(Long id, Throwable t) {
        throw new DbUnavailableException("DB call failed: findById(bookId=" + id + ")", t);
    }

    private List<BookDto> findAllFallback(Throwable t) {
        throw new DbUnavailableException("DB call failed: findAll(books)", t);
    }

    private BookDto insertFallback(String title, Long authorId, Set<Long> genresIds, Throwable t) {
        throw new DbUnavailableException("DB call failed: insert(book)", t);
    }

    private BookDto updateFallback(Long id, String title, Long authorId, Set<Long> genresIds, Throwable t) {
        throw new DbUnavailableException("DB call failed: update(bookId=" + id + ")", t);
    }

    private void deleteByIdFallback(Long id, Throwable t) {
        throw new DbUnavailableException("DB call failed: deleteById(bookId=" + id + ")", t);
    }
}
