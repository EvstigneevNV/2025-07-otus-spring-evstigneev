package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
