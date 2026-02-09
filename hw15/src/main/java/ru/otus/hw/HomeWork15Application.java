package ru.otus.hw;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.hw.domain.PizzaOrder;
import ru.otus.hw.domain.PizzaSize;
import ru.otus.hw.domain.PizzaType;
import ru.otus.hw.gateway.PizzaGateway;

import java.util.List;


@SpringBootApplication
public class HomeWork15Application {

	public static void main(String[] args) {
		SpringApplication.run(HomeWork15Application.class, args);
	}

    public CommandLineRunner demo(PizzaGateway gateway) {
        return args -> {
            var order = new PizzaOrder("Elena", PizzaType.PEPPERONI, PizzaSize.M, List.of("extra cheese"));
            var pizza = gateway.placeOrder(order);
            System.out.println("RESULT: " + pizza);
        };
    }
}
