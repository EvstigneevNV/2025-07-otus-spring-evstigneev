package ru.otus.hw.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.Pizza;
import ru.otus.hw.domain.PizzaOrder;

@MessagingGateway
public interface PizzaGateway {

    @Gateway(requestChannel = "orders.input", replyChannel = "orders.output")
    Pizza placeOrder(PizzaOrder order);
}
