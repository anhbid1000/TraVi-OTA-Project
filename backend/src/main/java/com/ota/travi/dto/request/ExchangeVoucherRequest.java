package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotNull;

public record ExchangeVoucherRequest(
        @NotNull(message = "ID voucher không được để trống")
        Long voucherId
) {
}
