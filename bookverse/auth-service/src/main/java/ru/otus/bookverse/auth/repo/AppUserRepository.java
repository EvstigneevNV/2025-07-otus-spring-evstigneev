package ru.otus.bookverse.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.bookverse.auth.entity.AppUser;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);
}
