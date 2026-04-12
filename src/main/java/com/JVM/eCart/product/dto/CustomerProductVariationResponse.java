package com.JVM.eCart.product.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record CustomerProductVariationResponse(
    Long variationId,
    BigDecimal price,
    Integer quantityAvailable,
    String primaryImage,
    List<String> secondaryImage,
    Map<String,String> metaData,
    Boolean isActive
) { }
