package com.JVM.eCart.customer.dto;

import com.JVM.eCart.seller.dto.AddressDto;

import java.util.List;

public record CustomerViewProfileResponse(
        Long id,
        String firstName,
        String lastName,
        Boolean isActive,
        List<AddressDto> contact
) { }
