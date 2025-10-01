package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document("author")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @Id
    private String id;

    private String fullName;

    @Override
    public boolean equals(Object o) {
        return o instanceof Author a && Objects.equals(id,a.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
