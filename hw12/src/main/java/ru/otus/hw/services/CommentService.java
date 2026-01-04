package ru.otus.hw.services;

import ru.otus.hw.dto.CommentsDto;
import java.util.List;

public interface CommentService {
    List<CommentsDto> findAllByBookId(Long id);

    CommentsDto findById(Long id);

    CommentsDto insert(String text, Long bookId);

    CommentsDto update(Long id ,String text, Long bookId);

    void deleteById(Long id);
}
