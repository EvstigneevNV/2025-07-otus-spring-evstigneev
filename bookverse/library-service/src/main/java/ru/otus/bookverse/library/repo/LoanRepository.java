package ru.otus.bookverse.library.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.bookverse.library.entity.Loan;
import ru.otus.bookverse.library.entity.LoanStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    List<Loan> findByUserIdOrderByBorrowedAtDesc(UUID userId);

    Optional<Loan> findByUserIdAndBookIdAndStatus(UUID userId, UUID bookId, LoanStatus status);

    @Query("select count(l) from Loan l where l.bookId = :bookId and l.status = 'ACTIVE'")
    long countActiveLoansByBook(@Param("bookId") UUID bookId);
}
