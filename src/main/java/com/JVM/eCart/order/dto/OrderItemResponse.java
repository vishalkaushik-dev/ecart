package com.JVM.eCart.order.dto;

import com.JVM.eCart.order.enums.OrderStatusEnum;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long orderProductId,
        Long productVariationId,
        String productName,
        String primaryImage,
        Integer quantity,
        BigDecimal price,
        OrderStatusEnum currentStatus
) { }
