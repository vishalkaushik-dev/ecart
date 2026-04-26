package com.JVM.eCart.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SingleOrderRequest(

        @NotNull(message = "Product variation id is required")
        Long productVariationId,

        @Min(value = 1, message = "Minimum 1 quantity is required")
        Integer quantity,

        @NotNull(message = "Address is required")
        Long addressId
) { }
