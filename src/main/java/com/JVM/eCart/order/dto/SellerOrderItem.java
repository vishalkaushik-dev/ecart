package com.JVM.eCart.order.dto;

import com.JVM.eCart.order.enums.OrderStatusEnum;

import java.math.BigDecimal;

public record SellerOrderItem(
        Long orderProductId,
        Integer quantity,
        BigDecimal price,
        String productName,
        String brand,
        String primaryImage,
        OrderStatusEnum status
) { }
