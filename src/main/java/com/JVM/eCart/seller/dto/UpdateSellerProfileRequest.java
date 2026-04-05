package com.JVM.eCart.seller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UpdateSellerProfileRequest(

        @Email(message = "Invalid email format")
        String email,
        String firstName,
        String lastName,
        String companyName,
        String companyContact,
        @Pattern(
                regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
                message = "Invalid GST number, GST should be valid as per Govt. norms"
        )
        String gst
) { }
