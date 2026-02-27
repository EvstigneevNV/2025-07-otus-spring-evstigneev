package ru.otus.bookverse.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.bookverse.library.dto.BookCreateRequest;
import ru.otus.bookverse.library.dto.BookResponse;
import ru.otus.bookverse.library.entity.Book;
import ru.otus.bookverse.library.repo.BookRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Cacheable("books")
    public List<BookResponse> getAll() {
        return bookRepository.findAll().stream().map(this::toResponse).toList();
    }

    public BookResponse getById(UUID id) {
        Book b = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        return toResponse(b);
    }

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public BookResponse create(BookCreateRequest request) {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title(request.title())
                .author(request.author())
                .publishYear(request.publishYear())
                .isbn(request.isbn())
                .totalCopies(request.totalCopies())
                .availableCopies(request.totalCopies())
                .build();
        return toResponse(bookRepository.save(book));
    }

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public BookResponse update(UUID id, BookCreateRequest request) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        int delta = request.totalCopies() - book.getTotalCopies();
        if (book.getAvailableCopies() + delta < 0) {
            throw new IllegalArgumentException("Total copies cannot be less than active loans");
        }

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublishYear(request.publishYear());
        book.setIsbn(request.isbn());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + delta);

        return toResponse(book);
    }

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public void delete(UUID id) {
        bookRepository.deleteById(id);
    }

    private BookResponse toResponse(Book b) {
        return new BookResponse(
                b.getId(),
                b.getTitle(),
                b.getAuthor(),
                b.getPublishYear(),
                b.getIsbn(),
                b.getTotalCopies(),
                b.getAvailableCopies()
        );
    }
}
