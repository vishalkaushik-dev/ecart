package com.JVM.eCart.category.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoryMetadataFieldValuesRequest(
        @NotNull(message = "Category Id is required")
        Long categoryId,

        @NotEmpty(message = "Field values cannot be empty")
        List<FieldValuesDto> fields
) { }
