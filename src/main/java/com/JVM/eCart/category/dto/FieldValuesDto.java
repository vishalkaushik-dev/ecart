package com.JVM.eCart.category.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FieldValuesDto(

        @NotNull
        Long fieldId,

        @NotEmpty
        List<String> values
) { }
