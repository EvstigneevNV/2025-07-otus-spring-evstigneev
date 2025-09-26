package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentsConverter;
import ru.otus.hw.dto.CommentsDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentsRepository commentsRepository;

    private final CommentsConverter commentsConverter;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentsDto> findAllByBookId(String id) {
        return commentsRepository.findAllByBookId(id).stream().map(commentsConverter::commentsToCommentsDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentsDto findById(String id) {
        return commentsRepository.findById(id).map(commentsConverter::commentsToCommentsDto)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(id)));
    }

    @Override
    @Transactional
    public CommentsDto insert(String text, String bookId) {

        return save(null, text, bookId);
    }

    @Override
    @Transactional
    public CommentsDto update(String id, String text, String bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentsRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllByBookId(String id) {
        commentsRepository.deleteByBookId(id);
    }

    private CommentsDto save (String id, String text, String bookId) {

        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));

        bookRepository.save(book);

        var comment = new Comment(id, text, book);
        return commentsConverter.commentsToCommentsDto(commentsRepository.save(comment));
    }
}
