package com.ota.travi.Dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        @NotBlank(message = "Email không được để trống")
        String email,

        @NotBlank(message = "Mật khẩu không đưược để trống")
        String matKhau,
        Boolean nhoMatKhau
){}
