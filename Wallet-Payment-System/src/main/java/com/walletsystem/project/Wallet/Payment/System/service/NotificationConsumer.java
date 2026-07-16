package com.walletsystem.project.Wallet.Payment.System.service;

import com.walletsystem.project.Wallet.Payment.System.config.RabbitMQConfig;
import com.walletsystem.project.Wallet.Payment.System.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleNotification(NotificationMessage message) {
        sendEmail(message.getTo(), message.getSubject(), message.getBody());
        System.out.println("Processed message from queue: " + message);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email", e);
        }
    }
}