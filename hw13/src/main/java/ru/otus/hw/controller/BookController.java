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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.request.UpsertBook;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookDto> list() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto get(@PathVariable Long id) {
        return bookService.findById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto create(@RequestBody UpsertBook req) {
        return bookService.insert(req.title(), req.authorId(), Set.copyOf(req.genreIds()));
    }

    @PutMapping("/{id}")
    public BookDto update(@PathVariable Long id, @RequestBody UpsertBook req) {
        return bookService.update(id, req.title(), req.authorId(), Set.copyOf(req.genreIds()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
