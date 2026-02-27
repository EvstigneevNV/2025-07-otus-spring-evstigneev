package ru.otus.bookverse.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.otus.bookverse.common.events.LoanEvent;

@Component
@RequiredArgsConstructor
public class LibraryEventListener {

    private final NotificationStore store;

    @RabbitListener(queues = "notifications")
    public void onEvent(LoanEvent event) {
        store.add(event);
    }
}
