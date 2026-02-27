package ru.otus.bookverse.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.otus.bookverse.auth.entity.AppUser;
import ru.otus.bookverse.auth.repo.AppUserRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        repository.findByEmail("admin@bookverse.local").orElseGet(() -> repository.save(
                AppUser.builder()
                        .id(UUID.randomUUID())
                        .email("admin@bookverse.local")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .roles("USER,ADMIN")
                        .build()
        ));
    }
}
