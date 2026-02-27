package ru.otus.bookverse.auth.dto;

public record TokenResponse(
        String tokenType,
        String accessToken,
        long expiresInSeconds
) {
}
