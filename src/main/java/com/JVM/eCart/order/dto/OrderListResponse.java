package com.JVM.eCart.order.dto;

import java.util.List;

public record OrderListResponse(
        List<OrderSummary> orders,
        int page,
        int size,
        long totalElements

) { }
