package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import ru.otus.hw.domain.Pizza;
import ru.otus.hw.domain.PizzaOrder;
import ru.otus.hw.domain.PizzaType;
import ru.otus.hw.service.KitchenService;

@Configuration
public class IntegrationConfig {

    @Bean
    public KitchenService kitchenService() {
        return new KitchenService();
    }

    @Bean
    public MessageChannel ordersInput() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel ordersOutput() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow pizzaFlow(KitchenService kitchen) {
        return IntegrationFlow.from("orders.input")
                .handle(PizzaOrder.class, (p, h) -> kitchen.validate(p))
                .enrichHeaders(h -> h.headerFunction("orderId",
                        m -> kitchen.generateOrderId((PizzaOrder) m.getPayload())))
                .publishSubscribeChannel(ps -> ps
                        .subscribe(sf -> sf.handle(m -> {
                            System.out.println("[TRACE] accepted order payload="
                                    + m.getPayload() + " headers=" + m.getHeaders());
                        }))
                )
                .route(PizzaOrder.class, PizzaOrder::type, r -> r
                        .subFlowMapping(PizzaType.MARGHERITA, margheritaSubflow(kitchen))
                        .subFlowMapping(PizzaType.PEPPERONI, pepperoniSubflow(kitchen))
                        .subFlowMapping(PizzaType.VEGGIE, veggieSubflow(kitchen))
                )
                .handle(Pizza.class, (pizza, headers) -> kitchen.pack(pizza))
                .channel("orders.output")
                .get();
    }

    private IntegrationFlow margheritaSubflow(KitchenService kitchen) {
        return f -> f
                .handle(PizzaOrder.class, (order, headers) ->
                        kitchen.prepareDough((String) headers.get("orderId"), order))
                .handle(Pizza.class, (pizza, headers) -> kitchen.bake(pizza));
    }

    private IntegrationFlow pepperoniSubflow(KitchenService kitchen) {
        return f -> f
                .handle(PizzaOrder.class, (order, headers) ->
                        kitchen.prepareDough((String) headers.get("orderId"), order))
                .handle(Pizza.class, (pizza, headers) -> {
                    System.out.println("[STEP] add pepperoni for orderId=" + headers.get("orderId"));
                    return pizza;
                })
                .handle(Pizza.class, (p, h) -> kitchen.bake(p));
    }

    private IntegrationFlow veggieSubflow(KitchenService kitchen) {
        return f -> f
                .handle(PizzaOrder.class, (order, headers) ->
                        kitchen.prepareDough((String) headers.get("orderId"), order))
                .handle(PizzaOrder.class, (order, headers) -> {
                    System.out.println("[STEP] add veggies for orderId=" + headers.get("orderId"));
                    return order;
                })
                .handle(Pizza.class, (p, h) -> kitchen.bake(p));
    }
}