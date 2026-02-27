package ru.otus.bookverse.library.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.otus.bookverse.library.dto.ProfileResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${clients.auth.base-url:http://localhost:8081}")
    private String authBaseUrl;

    @Retry(name = "authClient")
    @CircuitBreaker(name = "authClient", fallbackMethod = "fallbackProfile")
    public ProfileResponse profile(String bearerToken) {
        RestClient client = restClientBuilder.baseUrl(authBaseUrl).build();
        return client.get()
                .uri("/api/auth/profile")
                .header("Authorization", bearerToken)
                .retrieve()
                .body(ProfileResponse.class);
    }

    @SuppressWarnings("unused")
    private ProfileResponse fallbackProfile(String bearerToken, Throwable ex) {
        return new ProfileResponse(null, "unavailable", List.of());
    }
}
