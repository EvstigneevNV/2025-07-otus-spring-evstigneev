package ru.otus.bookverse.notification.service;

import org.springframework.stereotype.Component;
import ru.otus.bookverse.common.events.LoanEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NotificationStore {

    private final CopyOnWriteArrayList<LoanEvent> events = new CopyOnWriteArrayList<>();

    public void add(LoanEvent event) {
        events.add(0, event);
        if (events.size() > 2000) {
            events.remove(events.size() - 1);
        }
    }

    public List<LoanEvent> all() {
        return List.copyOf(events);
    }
}
