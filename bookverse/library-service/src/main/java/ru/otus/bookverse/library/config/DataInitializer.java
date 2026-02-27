package ru.otus.bookverse.library.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.otus.bookverse.library.entity.Book;
import ru.otus.bookverse.library.repo.BookRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final BookRepository bookRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (bookRepository.count() > 0) {
            return;
        }

        bookRepository.save(Book.builder()
                .id(UUID.randomUUID())
                .title("Clean Code")
                .author("Robert C. Martin")
                .publishYear(2008)
                .isbn("9780132350884")
                .totalCopies(3)
                .availableCopies(3)
                .build());

        bookRepository.save(Book.builder()
                .id(UUID.randomUUID())
                .title("Effective Java")
                .author("Joshua Bloch")
                .publishYear(2018)
                .isbn("9780134685991")
                .totalCopies(2)
                .availableCopies(2)
                .build());
    }
}
