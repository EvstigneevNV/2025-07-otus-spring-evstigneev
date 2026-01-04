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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.batch.dto.AuthorRow;
import ru.otus.hw.batch.dto.BookRow;
import ru.otus.hw.batch.dto.CommentRow;
import ru.otus.hw.batch.dto.GenreRow;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.mongo.AuthorDoc;
import ru.otus.hw.mongo.BookDoc;
import ru.otus.hw.mongo.CommentDoc;
import ru.otus.hw.mongo.GenreDoc;

import javax.sql.DataSource;
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
                               MongoTemplate mongoTemplate) {
        return new StepBuilder("cleanMongoStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
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
    public ItemProcessor<Author, AuthorDoc> authorToDocProcessor() {
        return a -> new AuthorDoc(String.valueOf(a.getId()), a.getFullName());
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
    public ItemProcessor<Genre, GenreDoc> genreToDocProcessor() {
        return g -> new GenreDoc(String.valueOf(g.getId()), g.getName());
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
    public ItemProcessor<Book, BookDoc> bookToDocProcessor() {
        return b -> {
            String authorId = b.getAuthor() == null ? null : String.valueOf(b.getAuthor().getId());
            List<String> genreIds = b.getGenres() == null
                    ? List.of()
                    : b.getGenres().stream().map(g -> String.valueOf(g.getId())).toList();
            return new BookDoc(String.valueOf(b.getId()), b.getTitle(), authorId, genreIds);
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
    public ItemProcessor<Comment, CommentDoc> commentToDocProcessor() {
        return c -> new CommentDoc(
                String.valueOf(c.getId()),
                c.getText(),
                c.getBook() == null ? null : String.valueOf(c.getBook().getId())
        );
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
    public ItemProcessor<AuthorDoc, AuthorRow> docToAuthorRowProcessor() {
        return d -> new AuthorRow(Long.valueOf(d.getId()), d.getFullName());
    }

    @Bean
    public JdbcBatchItemWriter<AuthorRow> authorRowWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<AuthorRow>()
                .dataSource(dataSource)
                .sql("insert into author(id, full_name) values (:id, :fullName)")
                .beanMapped()
                .build();
    }

    @Bean
    public Step mongoToJpaAuthorsStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      MongoPagingItemReader<AuthorDoc> mongoAuthorReader,
                                      ItemProcessor<AuthorDoc, AuthorRow> docToAuthorRowProcessor,
                                      JdbcBatchItemWriter<AuthorRow> authorRowWriter) {
        return new StepBuilder("mongoToJpaAuthorsStep", jobRepository)
                .<AuthorDoc, AuthorRow>chunk(CHUNK_SIZE, transactionManager)
                .reader(mongoAuthorReader)
                .processor(docToAuthorRowProcessor)
                .writer(authorRowWriter)
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
    public ItemProcessor<GenreDoc, GenreRow> docToGenreRowProcessor() {
        return d -> new GenreRow(Long.valueOf(d.getId()), d.getName());
    }

    @Bean
    public JdbcBatchItemWriter<GenreRow> genreRowWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<GenreRow>()
                .dataSource(dataSource)
                .sql("insert into genre(id, name) values (:id, :name)")
                .beanMapped()
                .build();
    }

    @Bean
    public Step mongoToJpaGenresStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     MongoPagingItemReader<GenreDoc> mongoGenreReader,
                                     ItemProcessor<GenreDoc, GenreRow> docToGenreRowProcessor,
                                     JdbcBatchItemWriter<GenreRow> genreRowWriter) {
        return new StepBuilder("mongoToJpaGenresStep", jobRepository)
                .<GenreDoc, GenreRow>chunk(CHUNK_SIZE, transactionManager)
                .reader(mongoGenreReader)
                .processor(docToGenreRowProcessor)
                .writer(genreRowWriter)
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
    public ItemProcessor<BookDoc, BookRow> docToBookRowProcessor() {
        return d -> new BookRow(
                Long.valueOf(d.getId()),
                d.getTitle(),
                d.getAuthorId() == null ? null : Long.valueOf(d.getAuthorId())
        );
    }

    @Bean
    public JdbcBatchItemWriter<BookRow> bookRowWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<BookRow>()
                .dataSource(dataSource)
                .sql("insert into book(id, title, author_id) values (:id, :title, :authorId)")
                .beanMapped()
                .build();
    }

    @Bean
    public Step mongoToJpaBooksStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    MongoPagingItemReader<BookDoc> mongoBookReader,
                                    ItemProcessor<BookDoc, BookRow> docToBookRowProcessor,
                                    JdbcBatchItemWriter<BookRow> bookRowWriter) {
        return new StepBuilder("mongoToJpaBooksStep", jobRepository)
                .<BookDoc, BookRow>chunk(CHUNK_SIZE, transactionManager)
                .reader(mongoBookReader)
                .processor(docToBookRowProcessor)
                .writer(bookRowWriter)
                .build();
    }


    @Bean
    public ItemWriter<BookDoc> bookGenresJoinWriter(JdbcTemplate jdbcTemplate) {
        return items -> {
            for (BookDoc book : items) {
                if (book.getGenreIds() == null || book.getGenreIds().isEmpty()) {
                    continue;
                }
                List<Object[]> batch = book.getGenreIds().stream()
                        .map(gid -> new Object[]{Long.valueOf(book.getId()), Long.valueOf(gid)})
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
    public ItemProcessor<CommentDoc, CommentRow> docToCommentRowProcessor() {
        return d -> new CommentRow(
                Long.valueOf(d.getId()),
                d.getText(),
                d.getBookId() == null ? null : Long.valueOf(d.getBookId())
        );
    }

    @Bean
    public JdbcBatchItemWriter<CommentRow> commentRowWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CommentRow>()
                .dataSource(dataSource)
                .sql("insert into comment(id, text, book_id) values (:id, :text, :bookId)")
                .beanMapped()
                .build();
    }

    @Bean
    public Step mongoToJpaCommentsStep(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       MongoPagingItemReader<CommentDoc> mongoCommentReader,
                                       ItemProcessor<CommentDoc, CommentRow> docToCommentRowProcessor,
                                       JdbcBatchItemWriter<CommentRow> commentRowWriter) {
        return new StepBuilder("mongoToJpaCommentsStep", jobRepository)
                .<CommentDoc, CommentRow>chunk(CHUNK_SIZE, transactionManager)
                .reader(mongoCommentReader)
                .processor(docToCommentRowProcessor)
                .writer(commentRowWriter)
                .build();
    }
}
