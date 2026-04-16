package com.JVM.eCart.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record ViewCartResponse(

        List<CartItemResponse> items,
        BigDecimal totalAmount
) { }
