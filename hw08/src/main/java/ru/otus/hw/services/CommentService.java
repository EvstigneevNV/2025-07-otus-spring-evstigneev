package ru.otus.hw.services;

import ru.otus.hw.dto.CommentsDto;
import java.util.List;

public interface CommentService {
    List<CommentsDto> findAllByBookId(String id);

    CommentsDto findById(String id);

    CommentsDto insert(String text, String bookId);

    CommentsDto update(String id ,String text, String bookId);

    void deleteById(String id);

    void deleteAllByBookId(String id);
}
