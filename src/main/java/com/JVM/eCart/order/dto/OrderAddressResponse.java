package com.JVM.eCart.order.dto;

public record OrderAddressResponse(
        String addressLine,
        String city,
        String state,
        String country,
        String label,
        String zipCode
) { }
