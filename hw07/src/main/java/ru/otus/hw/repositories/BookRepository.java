package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
