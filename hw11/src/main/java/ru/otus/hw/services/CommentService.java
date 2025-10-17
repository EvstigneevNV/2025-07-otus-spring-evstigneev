package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentsDto;

public interface CommentService {

    Flux<CommentsDto> findAllByBookId(String id);

    Mono<CommentsDto> findById(String id);

    Mono<CommentsDto> insert(String text, String bookId);

    Mono<CommentsDto> update(String id ,String text, String bookId);

    Mono<Void> deleteById(String id);
}
