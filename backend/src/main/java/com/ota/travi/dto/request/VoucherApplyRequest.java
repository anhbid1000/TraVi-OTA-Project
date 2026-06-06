package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VoucherApplyRequest(
        @NotNull(message = "ID booking không được để trống")
        Long bookingId,

        @NotBlank(message = "Mã voucher không được để trống")
        String maVoucher
) {
}
