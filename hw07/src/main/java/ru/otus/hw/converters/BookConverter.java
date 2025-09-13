package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    private final CommentsConverter commentsConverter;


    public BookDto bookToBookDto(Book book) {
        return new BookDto(book.getId(),
                book.getTitle(),
                authorConverter.authorToAuthorDto(book.getAuthor()),
                book.getGenres().stream().map(genreConverter::genreToGenreDto).toList(),
                book.getComments().stream().map(commentsConverter::commentsToCommentsDto).toList());
    }

    public String bookToString(BookDto bookDto) {
        var genresString = bookDto.genres().stream()
                .map(genreConverter::genreDtoToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        var commentsString = bookDto.comments().stream()
                .map(commentsConverter::commentsDtoToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));

        var result = "Id: %d, title: %s, author: {%s}, genres: [%s]".formatted(
                bookDto.id(),
                bookDto.title(),
                authorConverter.authorDtoToString(bookDto.author()),
                genresString);

        if (!bookDto.comments().isEmpty()) {
            result += ", comments: [{%s}]".formatted(commentsString);
        }

        return result;
    }

}
