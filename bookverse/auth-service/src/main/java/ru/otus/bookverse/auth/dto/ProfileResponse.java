package ru.otus.bookverse.auth.dto;

import java.util.List;
import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String email,
        List<String> roles
) {
}
