package com.ota.travi.dto.response;

public record AuthResponse(
        String token,
        String refreshToken,
        String type,
        String message
) {
}
