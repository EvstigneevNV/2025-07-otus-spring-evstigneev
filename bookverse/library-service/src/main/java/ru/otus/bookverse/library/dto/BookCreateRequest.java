package ru.otus.bookverse.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookCreateRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 255) String author,
        Integer publishYear,
        @NotBlank @Size(max = 32) String isbn,
        @NotNull @Min(1) Integer totalCopies
) {
}
