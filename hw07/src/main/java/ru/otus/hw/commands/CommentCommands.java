package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentsConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentsConverter commentsConverter;

    @ShellMethod(value = "Find all comments by book id", key = "ac")
    public String findAllBooks(Long id) {
        return commentService.findAllByBookId(id).stream()
                .map(commentsConverter::commentsDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find comments by id", key = "cbid")
    public String findBookById(Long id) {
        return commentsConverter.commentsDtoToString(commentService.findById(id));
    }

    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertBook(String text, Long bookId) {
        var savedBook = commentService.insert(text, bookId);
        return commentsConverter.commentsDtoToString(savedBook);
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateBook(Long id, String title, Long bookId) {
        var savedComm = commentService.update(id, title, bookId);
        return commentsConverter.commentsDtoToString(savedComm);
    }

    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteBook(Long id) {
        commentService.deleteById(id);
    }

}
