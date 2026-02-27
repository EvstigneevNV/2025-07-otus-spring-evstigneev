package ru.otus.bookverse.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.otus.bookverse.common.events.LoanEvent;

@Component
@RequiredArgsConstructor
public class LibraryEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${amqp.exchange:library.events}")
    private String exchange;

    public void publish(String routingKey, LoanEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
