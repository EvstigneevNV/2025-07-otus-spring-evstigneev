package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCommentsRepository implements CommentsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Comment> findAllByBookId(Long bookId) {
        return entityManager.createQuery("SELECT c FROM Comment c WHERE c.book.id = :bookId", Comment.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Comment.class, id));
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            entityManager.persist(comment);
            return comment;
        }
        return entityManager.merge(comment);
    }

    @Override
    public void deleteById(Long id) {
        entityManager.remove(entityManager.getReference(Comment.class, id));
    }
}
