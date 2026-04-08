package com.JVM.eCart.category.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(

        @NotBlank(message = "Category name is required")
        String name

) { }
