package com.JVM.eCart.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddProductRequest(

        @NotBlank(message = "Product name is required")
        String name,

        @NotBlank(message = "Brand name is required")
        String brand,

        @NotNull(message = "Category Id is required")
        Long categoryId,

        String description,
        Boolean isCancellable,
        Boolean isReturnable
) {
    public AddProductRequest {
        if (isCancellable == null) {
            isCancellable = false;
        }
        if (isReturnable == null) {
            isReturnable = false;
        }
    }
}
