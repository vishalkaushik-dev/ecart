package com.JVM.eCart.product.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record AdminVariationResponse(
        Long variationId,
        Integer quantityAvailable,
        BigDecimal price,
        String primaryImage,
        List<String> secondaryImages,
        Map<String, String> metadata,
        Boolean isActive
) { }
