package com.JVM.eCart.order.dto;

import com.JVM.eCart.user.entity.Address;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ViewOrderResponse(
        Long orderId,
        BigDecimal totalAmount,
        String paymentMethod,
        LocalDateTime dateCreated,
        OrderAddressResponse address,
        List<OrderItemResponse> items
) { }
