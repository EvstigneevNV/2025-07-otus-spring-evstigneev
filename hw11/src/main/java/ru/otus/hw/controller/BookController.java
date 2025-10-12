package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.request.UpsertBook;
import ru.otus.hw.services.BookService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public Flux<BookDto> list() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<BookDto> get(@PathVariable String id) {
        return bookService.findById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDto> create(@RequestBody UpsertBook req) {
        return bookService.insert(req.title(), req.authorId(), req.genreIds());
    }

    @PutMapping("/{id}")
    public Mono<BookDto> update(@PathVariable String id, @RequestBody UpsertBook req) {
        return bookService.update(id, req.title(), req.authorId(), req.genreIds());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return bookService.deleteById(id);
    }
}
