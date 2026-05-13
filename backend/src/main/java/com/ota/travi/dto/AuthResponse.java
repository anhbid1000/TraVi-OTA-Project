package com.ota.travi.dto;

public record AuthResponse (
        String token,
        String refreshToken,
        String type,
        String message
){}
