package ru.otus.bookverse.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.bookverse.auth.entity.AppUser;
import ru.otus.bookverse.auth.repo.AppUserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AppUser register(String email, String rawPassword) {
        repository.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("Email already registered");
        });

        AppUser user = AppUser.builder()
                .id(UUID.randomUUID())
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .roles("USER")
                .build();

        return repository.save(user);
    }

    public AppUser authenticate(String email, String rawPassword) {
        AppUser user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }

    public List<String> rolesList(AppUser user) {
        return Arrays.stream(user.getRoles().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
