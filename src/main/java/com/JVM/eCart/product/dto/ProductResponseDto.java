package com.JVM.eCart.product.dto;

public record ProductResponseDto(

        Long id,
        String name,
        String description,
        Boolean isActive,
        String brand,
        Boolean isDeleted,
        CategoryDto category,
        String createdBy

) {}
