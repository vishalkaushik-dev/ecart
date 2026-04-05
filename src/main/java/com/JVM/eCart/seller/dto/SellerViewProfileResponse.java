package com.JVM.eCart.seller.dto;

import java.util.List;

public record SellerViewProfileResponse(
        Long id,
        String firstName,
        String lastName,
        Boolean isActive,
        String companyContact,
        String companyName,
        String gst,

        List<AddressDto> addresses

) {
}
