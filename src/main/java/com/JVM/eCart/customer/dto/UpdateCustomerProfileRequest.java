package com.JVM.eCart.customer.dto;

import jakarta.validation.constraints.Email;

public record UpdateCustomerProfileRequest(

        @Email(message = "Invalid email format")
        String email,
        String firstName,
        String lastName
) { }
