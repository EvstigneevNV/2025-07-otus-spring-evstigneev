package ru.otus.hw.service;

import ru.otus.hw.domain.Pizza;
import ru.otus.hw.domain.PizzaOrder;

import java.util.UUID;

public class KitchenService {

    public PizzaOrder validate(PizzaOrder order) {
        if (order == null || order.customer() == null || order.customer().isBlank()) {
            throw new IllegalArgumentException("Customer is required");
        }
        return order;
    }

    public String generateOrderId(PizzaOrder order) {
        return UUID.randomUUID().toString();
    }

    public Pizza prepareDough(String orderId, PizzaOrder order) {
        return new Pizza(orderId, order.customer(), order.type(), order.size(), order.toppings(), "DOUGH_READY");
    }

    public Pizza bake(Pizza pizza) {
        return new Pizza(pizza.orderId(), pizza.customer(), pizza.type(), pizza.size(), pizza.toppings(), "BAKED");
    }

    public Pizza pack(Pizza pizza) {
        return new Pizza(pizza.orderId(), pizza.customer(), pizza.type(), pizza.size(), pizza.toppings(), "PACKED");
    }
}