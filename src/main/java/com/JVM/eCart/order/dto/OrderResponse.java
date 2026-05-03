package com.JVM.eCart.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        BigDecimal amountPaid,
        LocalDateTime dateCreated,
        String paymentMethod,
        List<SellerOrderItem> items
) { }
