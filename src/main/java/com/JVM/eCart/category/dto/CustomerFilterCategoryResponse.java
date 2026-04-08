package com.JVM.eCart.category.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CustomerFilterCategoryResponse(

        List<CategoryMetadataResponse> metadata,
        List<String> brands,
        BigDecimal minPrice,
        BigDecimal maxPrice
) { }
