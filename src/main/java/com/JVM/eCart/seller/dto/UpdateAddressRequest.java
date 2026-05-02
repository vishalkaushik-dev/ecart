package com.JVM.eCart.seller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAddressRequest(
        @NotBlank(message = "Address is required")
        String addressLine,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Country is required")
        String country,

        @NotBlank(message = "Label is required")
        String label,

        @NotBlank(message = "Zip code is required")
        String zipCode
) { }
