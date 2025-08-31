package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Репозиторий на основе Jdbc для работы с книгами ")
@JdbcTest
@Import({JdbcBookRepository.class, JdbcGenreRepository.class})
class JdbcBookRepositoryTest {

    @Autowired
    private JdbcBookRepository repositoryJdbc;

    @Autowired
    private NamedParameterJdbcOperations jdbc;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        System.out.println(repositoryJdbc.findAll());
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {
        var actualBook = repositoryJdbc.findById(expectedBook.getId());
        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = repositoryJdbc.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBook = new Book(0, "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(2)));
        var returnedBook = repositoryJdbc.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(repositoryJdbc.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = new Book(1L, "BookTitle_10500", dbAuthors.get(2),
                List.of(dbGenres.get(4), dbGenres.get(5)));

        assertThat(repositoryJdbc.findById(expectedBook.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedBook);

        var returnedBook = repositoryJdbc.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(repositoryJdbc.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertThat(repositoryJdbc.findById(1L)).isPresent();
        repositoryJdbc.deleteById(1L);
        assertThat(repositoryJdbc.findById(1L)).isEmpty();
    }

    @DisplayName("должен возвращать пустой Optional для несуществующей книги")
    @Test
    void shouldReturnEmptyWhenBookNotFound() {
        assertThat(repositoryJdbc.findById(999_999L)).isEmpty();
    }

    @DisplayName("должен вернуть пустой список, если таблица пуста")
    @Test
    void shouldReturnEmptyListWhenNoBooks() {
        // очистим связи и книги (в рамках транзакции теста — откатится)
        jdbc.update("DELETE FROM book_x_genre", Map.of());
        jdbc.update("DELETE FROM book", Map.of());

        assertThat(repositoryJdbc.findAll()).isEmpty();
    }

    @DisplayName("должен сохранять книгу без жанров (пустой список)")
    @Test
    void shouldSaveBookWithoutGenres() {
        var newBook = new Book(0L, "No genres here", dbAuthors.get(0), List.of());
        var saved = repositoryJdbc.save(newBook);

        assertThat(saved.getId()).isPositive();

        var loaded = repositoryJdbc.findById(saved.getId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getGenres()).isEmpty();
        assertThat(loaded.get().getAuthor()).isEqualTo(dbAuthors.get(0));
    }

    @DisplayName("должен игнорировать дубликаты жанров при вставке")
    @Test
    void shouldDeduplicateGenresOnInsert() {
        var g1 = dbGenres.get(0);
        var g2 = dbGenres.get(1);
        var withDuplicates = new Book(0L, "Dup genres", dbAuthors.get(1),
                List.of(g1, g1, g2, g2, g1));

        var saved = repositoryJdbc.save(withDuplicates);

        var reloaded = repositoryJdbc.findById(saved.getId());
        assertThat(reloaded).isPresent();

        // проверяем по id жанров — должно быть ровно два уникальных
        assertThat(reloaded.get().getGenres())
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(g1.getId(), g2.getId());
    }

    @DisplayName("должен полностью заменить набор жанров книги")
    @Test
    void shouldReplaceGenresOnUpdate() {
        var initial = new Book(0L, "Replace genres", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(1)));
        var saved = repositoryJdbc.save(initial);

        var updated = new Book(saved.getId(), saved.getTitle(), dbAuthors.get(0),
                List.of(dbGenres.get(2), dbGenres.get(3)));
        repositoryJdbc.save(updated);

        var reloaded = repositoryJdbc.findById(saved.getId());
        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getGenres())
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(dbGenres.get(2).getId(), dbGenres.get(3).getId());
    }

    @DisplayName("должен бросать EntityNotFoundException, если книги не существует")
    @Test
    void shouldThrowWhenUpdateNonExisting() {
        var notExisting = new Book(999_999L, "No such book", dbAuthors.get(0), List.of());
        assertThatThrownBy(() -> repositoryJdbc.save(notExisting))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book not found");
    }

    @DisplayName("должен бросать EntityNotFoundException, если книги не существует")
    @Test
    void shouldThrowWhenDeleteNonExisting() {
        assertThatThrownBy(() -> repositoryJdbc.deleteById(999_999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book not found");
    }

    @DisplayName("книга без связей жанров должна возвращаться с пустым списком жанров")
    @Test
    void shouldReadBookWithNoGenreRelationsAsEmptyList() {
        // вставим книгу напрямую без записей в book_x_genre
        var kh = new GeneratedKeyHolder();
        jdbc.update(
                "INSERT INTO book(title, author_id) VALUES(:title, :authorId)",
                new MapSqlParameterSource()
                        .addValue("title", "Raw insert no relations")
                        .addValue("authorId", dbAuthors.get(0).getId(), Types.BIGINT),
                kh
        );
        Long newId = kh.getKeyAs(Long.class);
        assertThat(newId).isNotNull();

        var opt = repositoryJdbc.findById(newId);
        assertThat(opt).isPresent();
        assertThat(opt.get().getGenres()).isEmpty();
        assertThat(opt.get().getTitle()).isEqualTo("Raw insert no relations");
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}