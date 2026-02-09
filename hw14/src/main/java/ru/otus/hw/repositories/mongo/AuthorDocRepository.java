package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.AuthorDoc;

public interface AuthorDocRepository extends MongoRepository<AuthorDoc, String> {
}
