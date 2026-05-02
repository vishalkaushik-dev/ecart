package com.JVM.eCart.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EmailEvent {
    private String to;
    private String subject;
    private String body;
}
