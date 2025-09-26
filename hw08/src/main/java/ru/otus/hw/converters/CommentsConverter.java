package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentsDto;
import ru.otus.hw.models.Comment;

@Component
public class CommentsConverter {

    public CommentsDto commentsToCommentsDto(Comment comment) {
        return new CommentsDto(comment.getId(), comment.getText());
    }

    public String commentsDtoToString(CommentsDto commentsDto) {
        return "Id: %s, text: %s"
                .formatted(
                        commentsDto.id(),
                        commentsDto.text()
                );
    }

}
