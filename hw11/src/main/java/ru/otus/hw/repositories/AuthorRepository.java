package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Author;

import java.util.List;

@Repository
public interface AuthorRepository extends ReactiveMongoRepository<Author, String> {

    Flux<Author> findByIdIn(List<String> ids);

}
