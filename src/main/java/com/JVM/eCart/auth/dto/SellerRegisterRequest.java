package com.JVM.eCart.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SellerRegisterRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
        String phoneNumber,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$",
                message = "Password must be at least 8 characters long and include at least one uppercase letter, one number, and one special character (@#$%^&+=)"
        )
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "GST no is required")
        @Pattern(
                regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
                message = "Invalid GST number, GST should be valid as per Govt. norms"
        )
        String gst,

        @NotBlank(message = "Company name is required")
        String companyName,

        @NotBlank(message = "Company address is required")
        String companyAddress,

        String addressLine,
        String city,
        String label,
        String state,
        String country,
        String zipCode
) {}
