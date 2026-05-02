package com.JVM.eCart.rabbitmq;

import com.JVM.eCart.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendEmail(String to, String subject, String body) {

        EmailEvent emailEvent = new EmailEvent(to, subject, body);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                emailEvent
        );
        log.info("Message sent: {} ", emailEvent);
    }

}
