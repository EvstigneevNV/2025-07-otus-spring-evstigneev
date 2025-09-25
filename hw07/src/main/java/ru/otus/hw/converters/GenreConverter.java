package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

@Component
public class GenreConverter {

    public GenreDto genreToGenreDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }

    public String genreDtoToString(GenreDto genreDto) {
        return "Id: %d, Name: %s"
                .formatted(
                        genreDto.id(),
                        genreDto.name()
                );
    }
}
