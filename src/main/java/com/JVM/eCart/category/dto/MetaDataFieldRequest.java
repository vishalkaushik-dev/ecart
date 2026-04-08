package com.JVM.eCart.category.dto;

import jakarta.validation.constraints.NotBlank;

public record MetaDataFieldRequest(

        @NotBlank(message = "Name cannot be empty")
        String name
) { }
