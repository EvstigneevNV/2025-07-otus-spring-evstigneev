package ru.otus.hw.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.bson.types.ObjectId;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.otus.hw.batch.mapping.EntityType;
import ru.otus.hw.batch.mapping.IdMappingService;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.mongo.AuthorDoc;
import ru.otus.hw.mongo.BookDoc;
import ru.otus.hw.mongo.CommentDoc;
import ru.otus.hw.mongo.GenreDoc;

import java.util.Collections;
import java.util.List;
 

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchJobsConfig {

    private static final int CHUNK_SIZE = 10;

    @Bean
    public Job jpaToMongoJob(JobRepository jobRepository, Step cleanMongoStep, Step jpaToMongoAuthorsStep,
                             Step jpaToMongoGenresStep, Step jpaToMongoBooksStep, Step jpaToMongoCommentsStep) {
        return new JobBuilder("jpaToMongoJob", jobRepository)
                .start(cleanMongoStep)
                .next(jpaToMongoAuthorsStep)
                .next(jpaToMongoGenresStep)
                .next(jpaToMongoBooksStep)
                .next(jpaToMongoCommentsStep)
                .build();
    }

    @Bean
    public Step cleanMongoStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               MongoTemplate mongoTemplate,
                               IdMappingService idMappingService) {
        return new StepBuilder("cleanMongoStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    idMappingService.clearAll();
                    dropIfExists(mongoTemplate, "comments");
                    dropIfExists(mongoTemplate, "books");
                    dropIfExists(mongoTemplate, "genres");
                    dropIfExists(mongoTemplate, "authors");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    private void dropIfExists(MongoTemplate mongoTemplate, String collection) {
        if (mongoTemplate.collectionExists(collection)) {
            mongoTemplate.dropCollection(collection);
        }
    }


    @Bean
    public JpaPagingItemReader<Author> jpaAuthorReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Author>()
                .name("jpaAuthorReader")
                .entityManagerFactory(emf)
                .queryString("select a from Author a order by a.id")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<Author, AuthorDoc> authorToDocProcessor(IdMappingService idMappingService) {
        return a -> {
            String mongoId = new ObjectId().toHexString();
            idMappingService.save(EntityType.AUTHOR, a.getId(), mongoId);
            return new AuthorDoc(mongoId, a.getFullName());
        };
    }

    @Bean
    public MongoItemWriter<AuthorDoc> mongoAuthorWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<AuthorDoc>()
                .collection("authors")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Step jpaToMongoAuthorsStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      JpaPagingItemReader<Author> jpaAuthorReader,
                                      ItemProcessor<Author, AuthorDoc> authorToDocProcessor,
                                      MongoItemWriter<AuthorDoc> mongoAuthorWriter) {
        return new StepBuilder("jpaToMongoAuthorsStep", jobRepository)
                .<Author, AuthorDoc>chunk(CHUNK_SIZE, transactionManager)
                .reader(jpaAuthorReader)
                .processor(authorToDocProcessor)
                .writer(mongoAuthorWriter)
                .build();
    }


    @Bean
    public JpaPagingItemReader<Genre> jpaGenreReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Genre>()
                .name("jpaGenreReader")
                .entityManagerFactory(emf)
                .queryString("select g from Genre g order by g.id")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<Genre, GenreDoc> genreToDocProcessor(IdMappingService idMappingService) {
        return g -> {
            String mongoId = new ObjectId().toHexString();
            idMappingService.save(EntityType.GENRE, g.getId(), mongoId);
            return new GenreDoc(mongoId, g.getName());
        };
    }

    @Bean
    public MongoItemWriter<GenreDoc> mongoGenreWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<GenreDoc>()
                .collection("genres")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Step jpaToMongoGenresStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     JpaPagingItemReader<Genre> jpaGenreReader,
                                     ItemProcessor<Genre, GenreDoc> genreToDocProcessor,
                                     MongoItemWriter<GenreDoc> mongoGenreWriter) {
        return new StepBuilder("jpaToMongoGenresStep", jobRepository)
                .<Genre, GenreDoc>chunk(CHUNK_SIZE, transactionManager)
                .reader(jpaGenreReader)
                .processor(genreToDocProcessor)
                .writer(mongoGenreWriter)
                .build();
    }


    @Bean
    public JpaPagingItemReader<Book> jpaBookReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Book>()
                .name("jpaBookReader")
                .entityManagerFactory(emf)
                .queryString("select b from Book b order by b.id")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<Book, BookDoc> bookToDocProcessor(IdMappingService idMappingService) {
        return b -> {
            String mongoId = new ObjectId().toHexString();
            idMappingService.save(EntityType.BOOK, b.getId(), mongoId);

            String authorId = b.getAuthor() == null ? null
                    : idMappingService.requireMongoId(EntityType.AUTHOR, b.getAuthor().getId());

            List<String> genreIds = b.getGenres() == null
                    ? List.of()
                    : b.getGenres().stream()
                        .map(g -> idMappingService.requireMongoId(EntityType.GENRE, g.getId()))
                        .toList();

            return new BookDoc(mongoId, b.getTitle(), authorId, genreIds);
        };
    }

    @Bean
    public MongoItemWriter<BookDoc> mongoBookWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<BookDoc>()
                .collection("books")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Step jpaToMongoBooksStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    JpaPagingItemReader<Book> jpaBookReader,
                                    ItemProcessor<Book, BookDoc> bookToDocProcessor,
                                    MongoItemWriter<BookDoc> mongoBookWriter) {
        return new StepBuilder("jpaToMongoBooksStep", jobRepository)
                .<Book, BookDoc>chunk(CHUNK_SIZE, transactionManager)
                .reader(jpaBookReader)
                .processor(bookToDocProcessor)
                .writer(mongoBookWriter)
                .build();
    }


    @Bean
    public JpaPagingItemReader<Comment> jpaCommentReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Comment>()
                .name("jpaCommentReader")
                .entityManagerFactory(emf)
                .queryString("select c from Comment c join fetch c.book order by c.id")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<Comment, CommentDoc> commentToDocProcessor(IdMappingService idMappingService) {
        return c -> {
            String mongoId = new ObjectId().toHexString();
            idMappingService.save(EntityType.COMMENT, c.getId(), mongoId);

            String bookId = c.getBook() == null ? null
                    : idMappingService.requireMongoId(EntityType.BOOK, c.getBook().getId());

            return new CommentDoc(mongoId, c.getText(), bookId);
        };
    }

    @Bean
    public MongoItemWriter<CommentDoc> mongoCommentWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<CommentDoc>()
                .collection("comments")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Step jpaToMongoCommentsStep(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       JpaPagingItemReader<Comment> jpaCommentReader,
                                       ItemProcessor<Comment, CommentDoc> commentToDocProcessor,
                                       MongoItemWriter<CommentDoc> mongoCommentWriter) {
        return new StepBuilder("jpaToMongoCommentsStep", jobRepository)
                .<Comment, CommentDoc>chunk(CHUNK_SIZE, transactionManager)
                .reader(jpaCommentReader)
                .processor(commentToDocProcessor)
                .writer(mongoCommentWriter)
                .build();
    }


    @Bean
    public Job mongoToJpaJob(JobRepository jobRepository, Step cleanJpaStep, Step mongoToJpaAuthorsStep,
                             Step mongoToJpaGenresStep, Step mongoToJpaBooksStep, Step mongoToJpaBooksGenresStep,
                             Step mongoToJpaCommentsStep) {
        return new JobBuilder("mongoToJpaJob", jobRepository)
                .start(cleanJpaStep)
                .next(mongoToJpaAuthorsStep)
                .next(mongoToJpaGenresStep)
                .next(mongoToJpaBooksStep)
                .next(mongoToJpaBooksGenresStep)
                .next(mongoToJpaCommentsStep)
                .build();
    }

    @Bean
    public Step cleanJpaStep(JobRepository jobRepository,
                             PlatformTransactionManager transactionManager,
                             JdbcTemplate jdbcTemplate) {
        return new StepBuilder("cleanJpaStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.update("delete from comment");
                    jdbcTemplate.update("delete from books_genres");
                    jdbcTemplate.update("delete from book");
                    jdbcTemplate.update("delete from genre");
                    jdbcTemplate.update("delete from author");
                    jdbcTemplate.update("delete from id_mapping");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


    @Bean
    public MongoPagingItemReader<AuthorDoc> mongoAuthorReader(MongoTemplate mongoTemplate) {
        return new MongoPagingItemReaderBuilder<AuthorDoc>()
                .name("mongoAuthorReader")
                .template(mongoTemplate)
                .targetType(AuthorDoc.class)
                .jsonQuery("{}")
                .sorts(Collections.singletonMap("_id", Sort.Direction.ASC))
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemWriter<AuthorDoc> mongoAuthorWriterToJpa(JdbcTemplate jdbcTemplate, IdMappingService idMappingService) {
        return items -> {
            for (AuthorDoc doc : items) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(con -> {
                    var ps = con.prepareStatement("insert into author(full_name) values (?)", new String[]{"id"});
                    ps.setString(1, doc.getFullName());
                    return ps;
                }, keyHolder);
                Number key = keyHolder.getKey();
                if (key == null) {
                    throw new IllegalStateException("Failed to insert author for mongoId=" + doc.getId());
                }
                idMappingService.save(EntityType.AUTHOR, key.longValue(), doc.getId());
            }
        };
    }

    @Bean
    public Step mongoToJpaAuthorsStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      MongoPagingItemReader<AuthorDoc> mongoAuthorReader,
                                      ItemWriter<AuthorDoc> mongoAuthorWriterToJpa) {
        return new StepBuilder("mongoToJpaAuthorsStep", jobRepository)
                .<AuthorDoc, AuthorDoc>chunk(CHUNK_SIZE, transactionManager)
                .reader(mongoAuthorReader)
                .writer(mongoAuthorWriterToJpa)
                .build();
    }


    @Bean
    public MongoPagingItemReader<GenreDoc> mongoGenreReader(MongoTemplate mongoTemplate) {
        return new MongoPagingItemReaderBuilder<GenreDoc>()
                .name("mongoGenreReader")
                .template(mongoTemplate)
                .targetType(GenreDoc.class)
                .jsonQuery("{}")
                .sorts(Collections.singletonMap("_id", Sort.Direction.ASC))
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemWriter<GenreDoc> mongoGenreWriterToJpa(JdbcTemplate jdbcTemplate, IdMappingService idMappingService) {
        return items -> {
            for (GenreDoc doc : items) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(con -> {
                    var ps = con.prepareStatement("insert into genre(name) values (?)", new String[]{"id"});
                    ps.setString(1, doc.getName());
                    return ps;
                }, keyHolder);
                Number key = keyHolder.getKey();
                if (key == null) {
                    throw new IllegalStateException("Failed to insert genre for mongoId=" + doc.getId());
                }
                idMappingService.save(EntityType.GENRE, key.longValue(), doc.getId());
            }
        };
    }

    @Bean
    public Step mongoToJpaGenresStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     MongoPagingItemReader<GenreDoc> mongoGenreReader,
                                     ItemWriter<GenreDoc> mongoGenreWriterToJpa) {
        return new StepBuilder("mongoToJpaGenresStep", jobRepository)
                .<GenreDoc, GenreDoc>chunk(CHUNK_SIZE, transactionManager)
                .reader(mongoGenreReader)
                .writer(mongoGenreWriterToJpa)
                .build();
    }


    @Bean
    public MongoPagingItemReader<BookDoc> mongoBookReader(MongoTemplate mongoTemplate) {
        return new MongoPagingItemReaderBuilder<BookDoc>()
                .name("mongoBookReader")
                .template(mongoTemplate)
                .targetType(BookDoc.class)
                .jsonQuery("{}")
                .sorts(Collections.singletonMap("_id", Sort.Direction.ASC))
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemWriter<BookDoc> mongoBookWriterToJpa(JdbcTemplate jdbcTemplate, IdMappingService idMappingService) {
        return items -> {
            for (BookDoc doc : items) {
                Long authorId = doc.getAuthorId() == null ? null
                        : idMappingService.requirePostgresId(EntityType.AUTHOR, doc.getAuthorId());

                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(con -> {
                    var ps = con.prepareStatement("insert into book(title, author_id) values (?, ?)",
                            new String[]{"id"});
                    ps.setString(1, doc.getTitle());
                    if (authorId == null) {
                        ps.setObject(2, null);
                    } else {
                        ps.setLong(2, authorId);
                    }
                    return ps;
                }, keyHolder);

                Number key = keyHolder.getKey();
                if (key == null) {
                    throw new IllegalStateException("Failed to insert book for mongoId=" + doc.getId());
                }
                idMappingService.save(EntityType.BOOK, key.longValue(), doc.getId());
            }
        };
    }

    @Bean
    public Step mongoToJpaBooksStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    MongoPagingItemReader<BookDoc> mongoBookReader,
                                    ItemWriter<BookDoc> mongoBookWriterToJpa) {
        return new StepBuilder("mongoToJpaBooksStep", jobRepository)
                .<BookDoc, BookDoc>chunk(CHUNK_SIZE, transactionManager)
                .reader(mongoBookReader)
                .writer(mongoBookWriterToJpa)
                .build();
    }


    @Bean
    public ItemWriter<BookDoc> bookGenresJoinWriter(JdbcTemplate jdbcTemplate, IdMappingService idMappingService) {
        return items -> {
            for (BookDoc book : items) {
                if (book.getGenreIds() == null || book.getGenreIds().isEmpty()) {
                    continue;
                }
                long bookPgId = idMappingService.requirePostgresId(EntityType.BOOK, book.getId());
                List<Object[]> batch = book.getGenreIds().stream()
                        .map(gid -> new Object[]{bookPgId, idMappingService
                                .requirePostgresId(EntityType.GENRE, gid)})
                        .toList();
                jdbcTemplate.batchUpdate("insert into books_genres(book, genre) values (?, ?)", batch);
            }
        };
    }

    @Bean
    public Step mongoToJpaBooksGenresStep(JobRepository jobRepository,
                                          PlatformTransactionManager transactionManager,
                                          MongoPagingItemReader<BookDoc> mongoBookReader,
                                          ItemWriter<BookDoc> bookGenresJoinWriter) {
        return new StepBuilder("mongoToJpaBooksGenresStep", jobRepository)
                .<BookDoc, BookDoc>chunk(CHUNK_SIZE, transactionManager)
                .reader(mongoBookReader)
                .writer(bookGenresJoinWriter)
                .build();
    }


    @Bean
    public MongoPagingItemReader<CommentDoc> mongoCommentReader(MongoTemplate mongoTemplate) {
        return new MongoPagingItemReaderBuilder<CommentDoc>()
                .name("mongoCommentReader")
                .template(mongoTemplate)
                .targetType(CommentDoc.class)
                .jsonQuery("{}")
                .sorts(Collections.singletonMap("_id", Sort.Direction.ASC))
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemWriter<CommentDoc> mongoCommentWriterToJpa(JdbcTemplate jdbcTemplate, IdMappingService idMappingService) {
        return items -> {
            for (CommentDoc doc : items) {
                Long bookId = doc.getBookId() == null ? null
                        : idMappingService.requirePostgresId(EntityType.BOOK, doc.getBookId());

                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(con -> {
                    var ps = con.prepareStatement("insert into comment(text, book_id) values (?, ?)",
                            new String[]{"id"});
                    ps.setString(1, doc.getText());
                    if (bookId == null) {
                        ps.setObject(2, null);
                    } else {
                        ps.setLong(2, bookId);
                    }
                    return ps;
                }, keyHolder);

                Number key = keyHolder.getKey();
                if (key == null) {
                    throw new IllegalStateException("Failed to insert comment for mongoId=" + doc.getId());
                }
                idMappingService.save(EntityType.COMMENT, key.longValue(), doc.getId());
            }
        };
    }

    @Bean
    public Step mongoToJpaCommentsStep(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       MongoPagingItemReader<CommentDoc> mongoCommentReader,
                                       ItemWriter<CommentDoc> mongoCommentWriterToJpa) {
        return new StepBuilder("mongoToJpaCommentsStep", jobRepository)
                .<CommentDoc, CommentDoc>chunk(CHUNK_SIZE, transactionManager)
                .reader(mongoCommentReader)
                .writer(mongoCommentWriterToJpa)
                .build();
    }
}
