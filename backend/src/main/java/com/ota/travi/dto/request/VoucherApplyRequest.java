package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VoucherApplyRequest(
        @NotBlank(message = "Mã voucher không được để trống")
        String voucherCode
) {}
