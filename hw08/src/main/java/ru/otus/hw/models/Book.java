package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document("book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    private String id;

    private String title;

    @DBRef(lazy = true)
    private Author author;

    @DBRef(lazy = true)
    private List<Genre> genres = List.of();

    @Override
    public boolean equals(Object o) {
        return o instanceof Book b && Objects.equals(id, b.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}