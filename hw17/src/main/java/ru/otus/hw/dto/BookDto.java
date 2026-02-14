package ru.otus.hw.dto;

import ru.otus.hw.models.Book;

import java.util.List;

public record BookDto(Long id, String title, AuthorDto author, List<GenreDto> genres, List<CommentsDto> comments) {

    public static BookDto bookToBookDto(Book book) {
        return new BookDto(book.getId(),
                book.getTitle(),
                AuthorDto.authorToAuthorDto(book.getAuthor()),
                book.getGenres().stream().map(GenreDto::genreToGenreDto).toList(),
                book.getComments().stream().map(CommentsDto::commentsToCommentsDto).toList());
    }

}
