package ru.otus.hw.dto;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

public record BookDto(String id, String title, AuthorDto author, List<GenreDto> genres, List<CommentsDto> comments) {

    public static BookDto bookToBookDto(Book book, Author author, List<Genre> genres, List<Comment> comments) {
        return new BookDto(book.getId(),
                book.getTitle(),
                AuthorDto.authorToAuthorDto(author),
                genres.stream().map(GenreDto::genreToGenreDto).toList(),
                comments.stream().map(CommentsDto::commentsToCommentsDto).toList());
    }

}
