package ru.otus.hw.migrations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

@ChangeUnit(id = "001-seed-authors-genres-books",
        order = "001",
        author = "init")
@RequiredArgsConstructor
@Component
public class InitDataChangelog {

    private final MongoTemplate mongo;

    @Execution
    public void execute() {
        var a1 = mongo.insert(new Author(null, "Author_1"));
        var a2 = mongo.insert(new Author(null, "Author_2"));
        var a3 = mongo.insert(new Author(null, "Author_3"));

        var g1 = mongo.insert(new Genre(null, "Genre_1"));
        var g2 = mongo.insert(new Genre(null, "Genre_2"));
        var g3 = mongo.insert(new Genre(null, "Genre_3"));
        var g4 = mongo.insert(new Genre(null, "Genre_4"));
        var g5 = mongo.insert(new Genre(null, "Genre_5"));
        var g6 = mongo.insert(new Genre(null, "Genre_6"));

        mongo.insert(new Book(null, "BookTitle_1", a1, List.of(g1, g2)));
        mongo.insert(new Book(null, "BookTitle_2", a2, List.of(g3, g4)));
        mongo.insert(new Book(null, "BookTitle_3", a3, List.of(g5, g6)));
    }

    @RollbackExecution
    public void rollback() {
        mongo.remove(new Query(), "book");
        mongo.remove(new Query(), "author");
        mongo.remove(new Query(), "genre");
    }
}
