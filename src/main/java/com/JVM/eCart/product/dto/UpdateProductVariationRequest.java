package com.JVM.eCart.product.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record UpdateProductVariationRequest(
        Integer quantityAvailable,
        BigDecimal price,
        Map<String,String> metaData,
        String primaryImage,
        List<String> secondaryImage,
        Boolean isActive
) { }
