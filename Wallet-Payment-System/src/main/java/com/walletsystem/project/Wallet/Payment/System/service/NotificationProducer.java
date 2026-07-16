package com.walletsystem.project.Wallet.Payment.System.service;

import com.walletsystem.project.Wallet.Payment.System.config.RabbitMQConfig;
import com.walletsystem.project.Wallet.Payment.System.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendNotification(NotificationMessage message) {
        // Set RabbitTemplate to use JSON conversion
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        // Send message to RabbitMQ exchange with routing key
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message
        );

        System.out.println("Sent message to queue: " + message);
    }
}