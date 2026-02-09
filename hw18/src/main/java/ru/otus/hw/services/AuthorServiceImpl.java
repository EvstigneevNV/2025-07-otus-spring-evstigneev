package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.DbUnavailableException;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    @CircuitBreaker(name = "db", fallbackMethod = "findAllFallback")
    @RateLimiter(name = "db")
    @Transactional
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream().map(AuthorDto::authorToAuthorDto).toList();
    }


    private List<AuthorDto> findAllFallback(Throwable t) {
        throw new DbUnavailableException("DB call failed: findAll(authors)", t);
    }
}
