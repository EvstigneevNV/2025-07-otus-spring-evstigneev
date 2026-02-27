package ru.otus.bookverse.common.events;

import java.time.Instant;
import java.util.UUID;

public record LoanEvent(
        UUID loanId,
        UUID bookId,
        String bookTitle,
        UUID userId,
        LoanEventType type,
        Instant occurredAt
) {
}
