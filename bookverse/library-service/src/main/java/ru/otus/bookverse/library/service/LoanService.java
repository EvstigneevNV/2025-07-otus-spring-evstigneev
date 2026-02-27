package ru.otus.bookverse.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.bookverse.common.events.LoanEvent;
import ru.otus.bookverse.common.events.LoanEventType;
import ru.otus.bookverse.library.dto.LoanResponse;
import ru.otus.bookverse.library.entity.Book;
import ru.otus.bookverse.library.entity.Loan;
import ru.otus.bookverse.library.entity.LoanStatus;
import ru.otus.bookverse.library.repo.BookRepository;
import ru.otus.bookverse.library.repo.LoanRepository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final LibraryEventPublisher eventPublisher;

    @Transactional
    public LoanResponse borrowBook(UUID bookId, UUID userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException("No available copies");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        Loan loan = Loan.builder()
                .id(UUID.randomUUID())
                .bookId(bookId)
                .userId(userId)
                .status(LoanStatus.ACTIVE)
                .borrowedAt(OffsetDateTime.now())
                .build();

        loanRepository.save(loan);

        eventPublisher.publish("loan.borrowed", new LoanEvent(
                loan.getId(),
                bookId,
                book.getTitle(),
                userId,
                LoanEventType.BORROWED,
                Instant.now()
        ));

        return toResponse(loan);
    }

    @Transactional
    public LoanResponse returnBook(UUID bookId, UUID userId) {
        Loan loan = loanRepository.findByUserIdAndBookIdAndStatus(userId, bookId, LoanStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Active loan not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedAt(OffsetDateTime.now());
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        eventPublisher.publish("loan.returned", new LoanEvent(
                loan.getId(),
                bookId,
                book.getTitle(),
                userId,
                LoanEventType.RETURNED,
                Instant.now()
        ));

        return toResponse(loan);
    }

    public List<LoanResponse> myLoans(UUID userId) {
        return loanRepository.findByUserIdOrderByBorrowedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private LoanResponse toResponse(Loan l) {
        return new LoanResponse(
                l.getId(),
                l.getBookId(),
                l.getUserId(),
                l.getStatus(),
                l.getBorrowedAt(),
                l.getReturnedAt()
        );
    }
}
