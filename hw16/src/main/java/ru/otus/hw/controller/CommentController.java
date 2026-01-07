package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.CommentsDto;
import ru.otus.hw.services.CommentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books/{bookId}/comments")
public class CommentController {
    private final CommentService commentService;

    public record UpsertComment(String text) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentsDto create(@PathVariable Long bookId, @RequestBody UpsertComment req) {
        return commentService.insert(req.text(), bookId);
    }

    @PutMapping("/{id}")
    public CommentsDto update(@PathVariable Long bookId, @PathVariable Long id, @RequestBody UpsertComment req) {
        return commentService.update(id, req.text(), bookId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long bookId, @PathVariable Long id) {
        commentService.deleteById(id);
    }
}
