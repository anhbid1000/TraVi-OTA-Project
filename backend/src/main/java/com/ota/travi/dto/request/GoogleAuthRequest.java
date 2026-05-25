package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequest(
        @NotBlank(message = "Google ID token không được để trống")
        String idToken,

        @NotBlank(message = "Loại tài khoản không được để trống")
        String loaiTaiKhoan
) {
}

