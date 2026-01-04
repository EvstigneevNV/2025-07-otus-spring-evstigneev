package ru.otus.hw.domain;

import java.util.List;

public record Pizza(
        String orderId,
        String customer,
        PizzaType type,
        PizzaSize size,
        List<String> toppings,
        String status
) {}
