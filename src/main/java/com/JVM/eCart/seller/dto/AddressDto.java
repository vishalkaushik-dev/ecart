package com.JVM.eCart.seller.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressDto(
        Long id,
        String addressLine,
        String city,
        String state,
        String country,
        String zipCode
) {}
