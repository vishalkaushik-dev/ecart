package com.JVM.eCart.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateProductRequest(

        @NotBlank(message = "Product name is required")
        @Pattern(
                regexp = "^[A-Za-z]+( [A-Za-z]+)*$",
                message = "Name must contain only letters and is required")
        String name,

        String description,
        Boolean isCancellable,
        Boolean isReturnable
) { }
