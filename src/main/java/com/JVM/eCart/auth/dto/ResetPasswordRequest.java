package com.JVM.eCart.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(

        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[0-9]).{6,}$",
                message = "Password must contain 1 uppercase and 1 number"
        )
        String password,

        @NotBlank(message = "Confirm Password is required")
        String confirmPassword
) {}
