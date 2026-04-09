package com.JVM.eCart.product.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductVariationResponse(

        Long variationId,
        BigDecimal price,
        Integer quantityAvailable,
        String primaryImage,
        List<String> secondaryImages,
        Map<String, String> metadata,
        Boolean isActive,

        // Parent Product Info
        Long productId,
        String productName,
        String productDescription

) {}
