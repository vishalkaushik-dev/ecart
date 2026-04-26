package com.JVM.eCart.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PartialOrderRequest(

        @NotNull(message = "Product Variation List is required")
        @NotEmpty(message = "At least one cart item must be selected")
        List<Long> productVariationIds,

        @NotNull(message = "Address is required")
        Long addressId
) { }
