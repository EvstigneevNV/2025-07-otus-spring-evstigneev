package ru.otus.bookverse.library.dto;

import java.util.UUID;

public record BookResponse(
        UUID id,
        String title,
        String author,
        Integer publishYear,
        String isbn,
        int totalCopies,
        int availableCopies
) {
}
