package com.JVM.eCart.product.dto;

import java.util.List;

public record CustomerProductResponse(
        Long productId,
        String name,
        String description,
        String brand,

        Long categoryId,
        String categoryName,

        List<CustomerProductVariationResponse> variations
) { }
