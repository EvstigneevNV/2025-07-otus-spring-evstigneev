package ru.otus.hw.dto;

import ru.otus.hw.models.Comment;

public record CommentsDto(Long id, String text) {

    public static CommentsDto commentsToCommentsDto(Comment comment) {
        return new CommentsDto(comment.getId(), comment.getText());
    }

}
