package com.JVM.eCart.rabbitmq;

import com.JVM.eCart.config.RabbitMQConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class EmailConsumer {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveMessage(EmailEvent event){
        log.info("Received email event for: {}", event.getTo());

        sendEmail(event.getTo(), event.getSubject(), event.getBody());
    }

    @Async
    private void sendEmail(String email, String subject, String mailContent) {
        log.info("Through RabbitMQ Sending email to: {}", email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(mailContent);
        mailSender.send(message);
    }
}
