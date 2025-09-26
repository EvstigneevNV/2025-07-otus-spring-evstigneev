package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentsDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
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
                book.getGenres().stream().map(genreConverter::genreToGenreDto).toList());
    }

    public String bookToString(BookDto bookDto){
        return bookToString(bookDto, List.of());
    }

    public String bookToString(BookDto bookDto, List<CommentsDto> comments) {
        var genresString = bookDto.genres().stream()
                .map(genreConverter::genreDtoToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        var commentsString = comments.stream()
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));

        var result = "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                bookDto.id(),
                bookDto.title(),
                authorConverter.authorDtoToString(bookDto.author()),
                genresString);

        if (!comments.isEmpty()) {
            result += ", comments: [{%s}]".formatted(commentsString);
        }

        return result;
    }

}
