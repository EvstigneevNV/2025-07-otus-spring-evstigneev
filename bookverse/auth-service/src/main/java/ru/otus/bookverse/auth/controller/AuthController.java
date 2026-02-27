package ru.otus.bookverse.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.otus.bookverse.auth.dto.*;
import ru.otus.bookverse.auth.entity.AppUser;
import ru.otus.bookverse.auth.service.JwtService;
import ru.otus.bookverse.auth.service.KeyPairProvider;
import ru.otus.bookverse.auth.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final KeyPairProvider keyPairProvider;

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request.email(), request.password());
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        AppUser user = userService.authenticate(request.email(), request.password());
        List<String> roles = userService.rolesList(user);

        String token = jwtService.issueToken(user.getId(), user.getEmail(), roles);
        return new TokenResponse("Bearer", token, jwtService.getTtlSeconds());
    }

    @GetMapping(value = "/jwks", produces = MediaType.APPLICATION_JSON_VALUE)
    public String jwks() {
        JWKSet jwkSet = keyPairProvider.getJwkSet();
        return jwkSet.toJSONObject().toString();
    }

    @GetMapping("/profile")
    public ProfileResponse profile(@AuthenticationPrincipal Jwt jwt) {
        UUID id = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        List<String> roles = jwt.getClaimAsStringList("roles");
        return new ProfileResponse(id, email, roles);
    }
}
