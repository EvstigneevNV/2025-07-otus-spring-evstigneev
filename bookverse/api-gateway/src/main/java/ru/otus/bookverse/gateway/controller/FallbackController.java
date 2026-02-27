package ru.otus.bookverse.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/library")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, Object> libraryFallback() {
        return Map.of(
                "error", "service_unavailable",
                "message", "library-service is temporarily unavailable"
        );
    }

    @GetMapping("/fallback/auth")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, Object> authFallback() {
        return Map.of(
                "error", "service_unavailable",
                "message", "auth-service is temporarily unavailable"
        );
    }
}
