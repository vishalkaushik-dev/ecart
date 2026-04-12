package com.JVM.eCart.product.dto;

public record UpdateProductRequest(
        String name,
        String description,
        Boolean isCancellable,
        Boolean isReturnable
) { }
