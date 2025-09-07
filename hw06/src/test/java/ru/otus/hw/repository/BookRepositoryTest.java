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
    TestEntityManager em;
    @Autowired
    BookRepository repo;

    @Test
    void findAllFetchesAuthorAndGenres() {
        var a = em.persist(new Author(null, "Author_1"));
        var g1 = em.persist(new Genre(null,  "G1"));
        var g2 = em.persist(new Genre(null,  "G2"));

        var b = new Book(null, "T1", a, new ArrayList<>(Set.of(g1, g2)), null);
        em.persist(b);

        em.flush();
        em.clear();

        var books = repo.findAll();
        assertThat(books).hasSize(1);
        var loaded = books.get(0);

        assertThat(loaded.getAuthor().getFullName()).isEqualTo("Author_1");
        assertThat(loaded.getGenres()).extracting(Genre::getName).containsExactlyInAnyOrder("G1","G2");
    }

    @Test
    void savePersistNewAndMergeExisting() {
        var a = em.persist(new Author(null, "A"));
        var b = new Book(null, "T", a, null, null);

        var saved = repo.save(b);
        assertThat(saved.getId()).isNotNull();

        saved.setTitle("T2");
        var merged = repo.save(saved);
        em.flush();
        em.clear();

        var reloaded = em.find(Book.class, merged.getId());
        assertThat(reloaded.getTitle()).isEqualTo("T2");
    }

    @Test
    void deleteByIdRemovesBook() {
        var a = em.persist(new Author(null, "A"));
        var b = em.persist(new Book(null, "T", a, null, null));
        em.flush();
        em.clear();

        repo.deleteById(b.getId());
        em.flush();
        em.clear();

        assertThat(em.find(Book.class, b.getId())).isNull();
    }
}