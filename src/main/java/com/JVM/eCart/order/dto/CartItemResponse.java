package com.JVM.eCart.order.dto;

import java.math.BigDecimal;
import java.util.Map;

public record CartItemResponse(
        long productVariationId,
        String productName,
        BigDecimal price,
        Integer quantity,
        String primaryImage,
        Map<String, String> metaData,
        Boolean isOutOfStock
) { }
