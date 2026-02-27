package ru.otus.bookverse.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.bookverse.common.events.LoanEvent;
import ru.otus.bookverse.notification.service.NotificationStore;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationStore store;

    @GetMapping
    public List<LoanEvent> all() {
        return store.all();
    }
}
