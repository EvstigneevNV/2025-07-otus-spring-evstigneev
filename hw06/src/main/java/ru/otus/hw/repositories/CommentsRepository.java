package ru.otus.hw.repositories;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentsRepository {

    List<Comment> findAllByBookId(Long id);

    Optional<Comment> findById(Long id);

    Comment save(Comment comment);

    void deleteById(Long id);
}
