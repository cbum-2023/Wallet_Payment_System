package com.walletsystem.project.Wallet.Payment.System.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "notificationExchange";
    public static final String QUEUE = "notificationQueue";
    public static final String ROUTING_KEY = "notification.email";

    // 1. Define Exchange
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    // 2. Define Queue
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true); // durable queue
    }

    // 3. Bind Queue to Exchange with Routing Key
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // 5. Configure RabbitTemplate to use JSON converter
    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}