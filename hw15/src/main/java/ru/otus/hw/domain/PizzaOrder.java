package ru.otus.hw.domain;

import java.util.List;

public record PizzaOrder(
        String customer,
        PizzaType type,
        PizzaSize size,
        List<String> toppings
) {}
