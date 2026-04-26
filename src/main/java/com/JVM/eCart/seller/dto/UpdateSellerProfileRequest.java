package com.JVM.eCart.seller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateSellerProfileRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Pattern(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Email must contain a valid domain (e.g., .com, .in)"
        )
        String email,

        @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
        @Size(min = 2, message = "First name must be at least 2 characters")
        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        @NotBlank(message = "Company name is required")
        String companyName,

        @NotBlank(message = "Company contact is required")
        String companyContact,

        @Pattern(
                regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
                message = "Invalid GST number, GST should be valid as per Govt. norms"
        )
        String gst
) { }
