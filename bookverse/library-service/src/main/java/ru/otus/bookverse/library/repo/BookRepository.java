package ru.otus.bookverse.library.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.bookverse.library.entity.Book;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
}
