package ru.otus.hw.dto;

import ru.otus.hw.models.Author;

public record AuthorDto(Long id, String fullName) {

    public static AuthorDto authorToAuthorDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }

}
