package com.JVM.eCart.user.dto;

public record RegisterRequest (
        String email,
        String phoneNumber,
        String password,
        String confirmPassword,
        String firstName,
        String lastName
) {}
