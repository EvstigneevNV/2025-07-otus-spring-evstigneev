package ru.otus.bookverse.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    @Bean
    public TopicExchange libraryExchange() {
        return new TopicExchange("library.events");
    }

    @Bean
    public Queue notificationsQueue() {
        return new Queue("notifications", true);
    }

    @Bean
    public Binding notificationsBinding(Queue notificationsQueue, TopicExchange libraryExchange) {
        return BindingBuilder.bind(notificationsQueue).to(libraryExchange).with("loan.*");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter mc) {
        RabbitTemplate rt = new RabbitTemplate(cf);
        rt.setMessageConverter(mc);
        return rt;
    }
}
