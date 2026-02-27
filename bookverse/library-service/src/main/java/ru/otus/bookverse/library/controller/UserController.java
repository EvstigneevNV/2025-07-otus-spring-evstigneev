package ru.otus.bookverse.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.bookverse.library.client.AuthClient;
import ru.otus.bookverse.library.dto.ProfileResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthClient authClient;

    @GetMapping("/me")
    public ProfileResponse me(@RequestHeader("Authorization") String authorization) {
        return authClient.profile(authorization);
    }
}
