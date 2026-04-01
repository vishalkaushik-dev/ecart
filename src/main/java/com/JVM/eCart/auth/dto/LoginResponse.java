package com.JVM.eCart.auth.dto;

public record LoginResponse(

        String accessToken,
        String tokenType,
        String message
) { }
