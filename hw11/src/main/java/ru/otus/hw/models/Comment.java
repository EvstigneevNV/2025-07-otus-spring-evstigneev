package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document("comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    private String id;

    private String text;

    private String bookId;

    @Override
    public boolean equals(Object o) {
        return o instanceof Comment c && Objects.equals(id,c.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
