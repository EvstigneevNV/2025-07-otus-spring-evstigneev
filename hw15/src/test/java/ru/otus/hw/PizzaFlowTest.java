package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.domain.PizzaOrder;
import ru.otus.hw.domain.PizzaSize;
import ru.otus.hw.domain.PizzaType;
import ru.otus.hw.gateway.PizzaGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PizzaFlowTest {

    @Autowired
    private PizzaGateway gateway;

    @Test
    void shouldProcessOrderEndToEnd() {
        var order = new PizzaOrder("Elena", PizzaType.MARGHERITA, PizzaSize.S, List.of());
        var pizza = gateway.placeOrder(order);

        assertThat(pizza).isNotNull();
        assertThat(pizza.status()).isEqualTo("PACKED");
        assertThat(pizza.customer()).isEqualTo("Elena");
    }
}
