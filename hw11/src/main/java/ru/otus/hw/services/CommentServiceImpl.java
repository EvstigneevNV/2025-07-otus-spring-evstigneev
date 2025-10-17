package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentsDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentsRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentsRepository commentsRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<CommentsDto> findAllByBookId(String id) {
        return commentsRepository.findAllByBookId(id).map(CommentsDto::commentsToCommentsDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CommentsDto> findById(String id) {
        return commentsRepository.findById(id).map(CommentsDto::commentsToCommentsDto)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Comment with id %s not found".formatted(id))));
    }

    @Override
    @Transactional
    public Mono<CommentsDto> insert(String text, String bookId) {
        return save(null, text, bookId);
    }

    @Override
    @Transactional
    public Mono<CommentsDto> update(String id, String text, String bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(String id) {
        return commentsRepository.deleteById(id);
    }

    private Mono<CommentsDto> save (String id, String text, String bookId) {

        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s not found".formatted(bookId))))
                .map(book -> new Comment(id, text, bookId))
                .flatMap(commentsRepository::save)
                .map(CommentsDto::commentsToCommentsDto);

    }
}
