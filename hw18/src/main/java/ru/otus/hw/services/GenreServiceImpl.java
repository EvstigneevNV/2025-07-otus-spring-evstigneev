package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.DbUnavailableException;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;


    @Override
    @CircuitBreaker(name = "db", fallbackMethod = "findAllFallback")
    @RateLimiter(name = "db")
    @Transactional
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream().map(GenreDto::genreToGenreDto).toList();
    }


    private List<GenreDto> findAllFallback(Throwable t) {
        throw new DbUnavailableException("DB call failed: findAll(genres)", t);
    }
}
