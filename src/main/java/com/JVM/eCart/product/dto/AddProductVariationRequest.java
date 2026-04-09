package com.JVM.eCart.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record AddProductVariationRequest(

        @NotNull
        Long productId,

        @NotNull
        Map<String, String> metadata, // e.g. { "size": "M", "color": "Red" }

        @NotNull
        @Min(0)
        Integer quantityAvailable,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal price,

        @NotBlank
        String primaryImageName,

        List<String> secondaryImages
) { }
