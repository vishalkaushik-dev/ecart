package com.JVM.eCart.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummary(
        Long orderId,
        BigDecimal totalAmount,
        String paymentMethod,
        LocalDateTime dateCreated
//        String currentStatus
) { }
