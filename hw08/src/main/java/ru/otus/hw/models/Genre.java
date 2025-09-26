package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;


@Document("genre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @Id
    private String id;

    private String name;

    @Override
    public boolean equals(Object o) {
        return o instanceof Genre g && Objects.equals(id,g.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
