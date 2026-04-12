package com.JVM.eCart.product.dto;

import java.util.List;

public record CustomerAllProductsResponse(
        Long productId,
        String name,
        String description,
        String brand,
        CustomerAllProductsCategoryResponse category,
        List<String> primaryImage
) { }
