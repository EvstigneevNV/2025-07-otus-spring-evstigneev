package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.CommentDoc;

public interface CommentDocRepository extends MongoRepository<CommentDoc, String> {
}
