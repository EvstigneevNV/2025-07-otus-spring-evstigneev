package ru.otus.bookverse.library.dto;

import ru.otus.bookverse.library.entity.LoanStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        UUID bookId,
        UUID userId,
        LoanStatus status,
        OffsetDateTime borrowedAt,
        OffsetDateTime returnedAt
) {
}
