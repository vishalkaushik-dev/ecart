package com.JVM.eCart.auth.dto;

public record LoginResponse(

        String accessToken,
        String refreshToken,
        String tokenType,
        String message
) { }
