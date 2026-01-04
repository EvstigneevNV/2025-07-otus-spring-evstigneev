package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final BookRepository bookRepository;

    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Transactional(readOnly = true)
    public List<CommentsDto> findAllByBookId(Long id) {
        return commentsRepository.findAllByBookId(id).stream().map(CommentsDto::commentsToCommentsDto).toList();
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Transactional(readOnly = true)
    public CommentsDto findById(Long id) {
        return commentsRepository.findById(id).map(CommentsDto::commentsToCommentsDto)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Transactional
    public CommentsDto insert(String text, Long bookId) {
        return save(null, text, bookId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CommentsDto update(Long id, String text, Long bookId) {
        return save(id, text, bookId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteById(Long id) {
        commentsRepository.deleteById(id);
    }

    private CommentsDto save (Long id, String text, Long bookId) {

        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));


        var comment = new Comment(id, text, book);
        return CommentsDto.commentsToCommentsDto(commentsRepository.save(comment));
    }
}
