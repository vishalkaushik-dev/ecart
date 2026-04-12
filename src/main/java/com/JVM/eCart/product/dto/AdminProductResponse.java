package com.JVM.eCart.product.dto;

import java.util.List;

public record AdminProductResponse(
        Long productId,
        String name,
        String description,
        String brand,
        Boolean isCancellable,
        Boolean isReturnable,
        Boolean isActive,
        Boolean isDeleted,
        AdminCategoryResponse category,
        List<AdminVariationResponse> variations
) { }
