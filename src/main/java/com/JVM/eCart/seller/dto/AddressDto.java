package com.JVM.eCart.seller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressDto(

        @NotBlank(message = "Id can not be null while updating address")
        Long id,

        @NotNull
        String addressLine,

        @NotNull
        String city,

        @NotNull
        String state,

        @NotNull
        String country,

        @NotNull
        String label,

        @NotNull
        String zipCode
) {}
