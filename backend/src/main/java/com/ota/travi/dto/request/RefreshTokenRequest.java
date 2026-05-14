package com.ota.travi.dto;


import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh Token không được để trống")
        String refreshToken
) {}