package com.JVM.eCart.customer.dto;

import jakarta.validation.constraints.*;

public record UpdateCustomerProfileRequest(

//        @NotBlank(message = "Email is required")
//        @Email(message = "Invalid email format")
//        @Pattern(
//                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
//                message = "Email must contain a valid domain (e.g., .com, .in)"
//        )
//        String email,

        @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
        @Size(min = 2, message = "First name must be at least 2 characters")
        @NotBlank(message = "First name is required")
        String firstName,

        @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only letters")
        String lastName,

        @NotNull(message = "Phone number is required")
        @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
        String phoneNumber
) { }
