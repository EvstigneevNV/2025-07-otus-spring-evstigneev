package ru.otus.hw;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseMongoTest {

    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:6.0");

    static {
        MONGO.start();
    }
    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry r) {
        r.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
        r.add("spring.data.mongodb.auto-index-creation", () -> "true");
        r.add("spring.shell.interactive.enabled", () -> "false");
        r.add("spring.shell.script.enabled", () -> "false");
        r.add("mongock.enabled", () -> "false");
    }

    @Autowired
    private MongoTemplate mongo;

    @BeforeEach
    void cleanBefore() { mongo.getCollectionNames().forEach(mongo::dropCollection); }

    @AfterEach
    void cleanAfter()  { mongo.getCollectionNames().forEach(mongo::dropCollection); }
}
