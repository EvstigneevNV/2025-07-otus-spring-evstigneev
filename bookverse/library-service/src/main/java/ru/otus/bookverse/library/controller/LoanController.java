package ru.otus.bookverse.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.otus.bookverse.library.dto.LoanResponse;
import ru.otus.bookverse.library.service.LoanService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/{bookId}")
    public LoanResponse borrow(@PathVariable UUID bookId, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return loanService.borrowBook(bookId, userId);
    }

    @PostMapping("/{bookId}/return")
    public LoanResponse returnBook(@PathVariable UUID bookId, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return loanService.returnBook(bookId, userId);
    }

    @GetMapping("/my")
    public List<LoanResponse> my(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return loanService.myLoans(userId);
    }
}
