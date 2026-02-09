package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.DbUnavailableException;
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
    @CircuitBreaker(name = "db", fallbackMethod = "findAllByBookIdFallback")
    @RateLimiter(name = "db")
    @Transactional(readOnly = true)
    public List<CommentsDto> findAllByBookId(Long id) {
        return commentsRepository.findAllByBookId(id).stream().map(CommentsDto::commentsToCommentsDto).toList();
    }

    @Override
    @CircuitBreaker(name = "db", fallbackMethod = "findByIdFallback")
    @RateLimiter(name = "db")
    @Transactional(readOnly = true)
    public CommentsDto findById(Long id) {
        return commentsRepository.findById(id).map(CommentsDto::commentsToCommentsDto)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));
    }

    @Override
    @CircuitBreaker(name = "db", fallbackMethod = "insertFallback")
    @RateLimiter(name = "db")
    @Transactional
    public CommentsDto insert(String text, Long bookId) {
        return save(null, text, bookId);
    }

    @Override
    @CircuitBreaker(name = "db", fallbackMethod = "updateFallback")
    @RateLimiter(name = "db")
    @Transactional
    public CommentsDto update(Long id, String text, Long bookId) {
        return save(id, text, bookId);
    }

    @Override
    @CircuitBreaker(name = "db", fallbackMethod = "deleteByIdFallback")
    @RateLimiter(name = "db")
    @Transactional
    public void deleteById(Long id) {
        commentsRepository.deleteById(id);
    }

    private CommentsDto save(Long id, String text, Long bookId) {

        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));


        var comment = new Comment(id, text, book);
        return CommentsDto.commentsToCommentsDto(commentsRepository.save(comment));
    }


    private List<CommentsDto> findAllByBookIdFallback(Long id, Throwable t) {
        throw new DbUnavailableException("DB call failed: findAllByBookId(bookId=" + id + ")", t);
    }

    private CommentsDto findByIdFallback(Long id, Throwable t) {
        throw new DbUnavailableException("DB call failed: findById(commentId=" + id + ")", t);
    }

    private CommentsDto insertFallback(String text, Long bookId, Throwable t) {
        throw new DbUnavailableException("DB call failed: insert(comment)", t);
    }

    private CommentsDto updateFallback(Long id, String text, Long bookId, Throwable t) {
        throw new DbUnavailableException("DB call failed: update(commentId=" + id + ")", t);
    }

    private void deleteByIdFallback(Long id, Throwable t) {
        throw new DbUnavailableException("DB call failed: deleteById(commentId=" + id + ")", t);
    }
}
