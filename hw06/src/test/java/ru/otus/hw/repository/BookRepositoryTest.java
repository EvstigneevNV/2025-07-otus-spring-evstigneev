package ru.otus.hw.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.JpaBookRepository;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaBookRepository.class)
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    BookRepository bookRepository;

    @Test
    void findAllFetchesAuthorAndGenres() {
        var author = testEntityManager.persist(new Author(null, "Author_1"));
        var genre1 = testEntityManager.persist(new Genre(null,  "G1"));
        var genre2 = testEntityManager.persist(new Genre(null,  "G2"));

        var book = new Book(null, "T1", author, new ArrayList<>(Set.of(genre1, genre2)));
        testEntityManager.persist(book);

        testEntityManager.flush();
        testEntityManager.clear();

        var books = bookRepository.findAll();
        assertThat(books).hasSize(1);
        var loaded = books.get(0);

        assertThat(loaded.getAuthor().getFullName()).isEqualTo("Author_1");
        assertThat(loaded.getGenres()).extracting(Genre::getName).containsExactlyInAnyOrder("G1","G2");
    }

    @Test
    void savePersistNewAndMergeExisting() {
        var author = testEntityManager.persist(new Author(null, "A"));
        var book = new Book(null, "T", author, null);

        var saved = bookRepository.save(book);
        assertThat(saved.getId()).isNotNull();

        saved.setTitle("T2");
        var merged = bookRepository.save(saved);
        testEntityManager.flush();
        testEntityManager.clear();

        var reloaded = testEntityManager.find(Book.class, merged.getId());
        assertThat(reloaded.getTitle()).isEqualTo("T2");
    }

    @Test
    void deleteByIdRemovesBook() {
        var author = testEntityManager.persist(new Author(null, "A"));
        var book = testEntityManager.persist(new Book(null, "T", author, null));
        testEntityManager.flush();
        testEntityManager.clear();

        bookRepository.deleteById(book.getId());
        testEntityManager.flush();
        testEntityManager.clear();

        assertThat(testEntityManager.find(Book.class, book.getId())).isNull();
    }
}