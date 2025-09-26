package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentsDto;
import java.util.List;

public interface CommentService {
    List<CommentsDto> findAllByBookId(String id);

    CommentsDto findById(String id);

    CommentsDto insert(String text, String bookId);

    CommentsDto update(String id ,String text, String bookId);

    void deleteById(String id);

    @Transactional
    void deleteAllByBookId(String id);
}
