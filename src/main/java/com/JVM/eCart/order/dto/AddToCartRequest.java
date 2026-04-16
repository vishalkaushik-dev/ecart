package com.JVM.eCart.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(
        @NotNull(message = "Product variation id is required")
        Long productVariationId,

        @NotNull
        @Min(value = 1, message = "Quantity should be greater than 0")
        Integer quantity
) { }
