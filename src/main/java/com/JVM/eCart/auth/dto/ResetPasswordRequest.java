package com.JVM.eCart.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$",
                message = "Password must be at least 8 characters long and include at least one uppercase letter, one number, and one special character (@#$%^&+=)"
        )
        String password,

        @NotBlank(message = "Confirm Password is required")
        String confirmPassword
) {}
