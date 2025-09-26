package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorConverter {

    public AuthorDto authorToAuthorDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }

    public String authorDtoToString(AuthorDto authorDto) {
        return "Id: %s, FullName: %s"
                .formatted(
                        authorDto.id(),
                        authorDto.fullName()
                );
    }
}
