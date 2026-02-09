package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.BookDoc;

public interface BookDocRepository extends MongoRepository<BookDoc, String> {
}
