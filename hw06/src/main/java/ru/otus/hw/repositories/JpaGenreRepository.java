package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@Repository
public class JpaGenreRepository implements GenreRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Genre> findAll() {
        return entityManager.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        return entityManager.createQuery("SELECT g FROM Genre g WHERE g.id IN (:id)", Genre.class)
                .setParameter("id", ids).getResultList();
    }

}
