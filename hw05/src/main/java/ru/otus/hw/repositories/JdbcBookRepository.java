package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations jdbc;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        String sql = """
                SELECT b.id AS b_id,
                       b.title AS b_title,
                       a.id AS a_id,
                       a.full_name AS a_full_name,
                       g.id AS g_id,
                       g.name AS g_name
                FROM book b
                LEFT JOIN author a ON a.id = b.author_id
                LEFT JOIN book_x_genre bxg ON bxg.book = b.id
                LEFT JOIN genre g ON g.id = bxg.genre
                WHERE b.id = :id
                """;

        return Optional.ofNullable(jdbc.query(
                sql,
                Map.of("id", id),
                new BookResultSetExtractor()));
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        jdbc.update(
                "DELETE FROM book_x_genre WHERE book = :id",
                new MapSqlParameterSource("id", id)
        );

        int affected = jdbc.update(
                "DELETE FROM book WHERE id = :id",
                new MapSqlParameterSource("id", id)
        );

        if (affected == 0) {
            throw new EntityNotFoundException("Book not found id=" + id);
        }
    }

    private List<Book> getAllBooksWithoutGenres() {
        String sql = """
                SELECT b.id AS b_id,
                       b.title AS b_title,
                       a.id AS a_id,
                       a.full_name AS a_full_name
                FROM book b
                LEFT JOIN author a ON a.id = b.author_id
                """;

        return jdbc.query(
                sql,
                Map.of(),
                new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        String sql = """
                SELECT bxg.book AS b_id,
                       bxg.genre AS  g_id
                FROM book_x_genre bxg
                """;
        return jdbc.query(
                sql,
                Map.of(),
                new BookGenreRelationMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        if (booksWithoutGenres == null || genres == null || relations == null) {
            return;
        }


        Map<Long, Book> bookById = booksWithoutGenres.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Book::getId, b -> b, (a, b) -> a, LinkedHashMap::new));

        Map<Long, Genre> genreById = genres.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Genre::getId, g -> g, (a, b) -> a, LinkedHashMap::new));

        Map<Long, LinkedHashSet<Genre>> genresByBook = new HashMap<>();

        for (BookGenreRelation rel : relations) {
            if (rel == null) continue;
            Book book = bookById.get(rel.bookId());
            Genre genre = genreById.get(rel.genreId());
            if (book == null || genre == null) {
                continue;
            }
            genresByBook
                    .computeIfAbsent(rel.bookId(), k -> new LinkedHashSet<>())
                    .add(genre);
        }

        for (Book book : booksWithoutGenres) {
            if (book == null) continue;
            LinkedHashSet<Genre> set = genresByBook.get(book.getId());
            book.setGenres(set == null ? new ArrayList<>() : new ArrayList<>(set));
        }
    }

    private Book insert(Book book) {
        final String sql = """
        INSERT INTO book(title, author_id)
        VALUES (:title, :authorId)
        """;

        var keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId(), Types.BIGINT);

        jdbc.update(sql, params, keyHolder);

        book.setId(keyHolder.getKeyAs(Long.class));

        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        final String sql = """
        UPDATE book
        SET title = :title,
            author_id = :authorId
        WHERE id = :id
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", book.getId())
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId(), Types.BIGINT);

        int updated = jdbc.update(sql, params);
        if (updated == 0) {
            throw new EntityNotFoundException("Book not found id=" + book.getId());
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        List<Genre> genres = book.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }

        List<Long> genreIds = genres.stream()
                .filter(Objects::nonNull)
                .map(Genre::getId)
                .distinct()
                .toList();

        if (genreIds.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO book_x_genre(book, genre) VALUES (:book, :genre)";

        SqlParameterSource[] batch = genreIds.stream()
                .map(gid -> new MapSqlParameterSource()
                        .addValue("book", book.getId())
                        .addValue("genre", gid))
                .toArray(SqlParameterSource[]::new);

        jdbc.batchUpdate(sql, batch);
    }

    private void removeGenresRelationsFor(Book book) {
        final String sql = "DELETE FROM book_x_genre WHERE book = :book";
        jdbc.update(sql, new MapSqlParameterSource("book", book.getId()));
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Book(
                    rs.getLong("b_id"),
                    rs.getString("b_title"),
                    new Author(rs.getLong("a_id"), rs.getString("a_full_name")),
                    List.of());
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.next()) {
                return null;
            }
            var book = new Book(
                    rs.getLong("b_id"),
                    rs.getString("b_title"),
                    new Author(rs.getLong("a_id"), rs.getString("a_full_name")),
                    null);
            List<Genre> genres = new ArrayList<>();
            do {
                if(rs.getLong("g_id") != 0 && !rs.getString("g_name").isEmpty())
                    genres.add(new Genre(rs.getLong("g_id"), rs.getString("g_name")));
            } while (rs.next());
            book.setGenres(genres);
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRelationMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BookGenreRelation(
                    rs.getLong("b_id"),
                    rs.getLong("g_id"));
        }
    }
}


