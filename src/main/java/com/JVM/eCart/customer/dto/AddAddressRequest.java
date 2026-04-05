package com.JVM.eCart.customer.dto;

import jakarta.validation.constraints.NotBlank;

public record AddAddressRequest(
        @NotBlank(message = "Address line is required")
        String addressLine,
        @NotBlank(message = "City is required")
        String city,
        @NotBlank(message = "State is required")
        String state,
        @NotBlank(message = "Country is required")
        String country,
        @NotBlank(message = "Zip Code is required")
        String zipCode,
        @NotBlank(message = "Label is required")
        String label
) { }
