package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.GenreDoc;

public interface GenreDocRepository extends MongoRepository<GenreDoc, String> {
}
