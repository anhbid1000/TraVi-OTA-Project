package com.ota.travi.dto.response;

public record VoucherApplyResponse(
        String maVoucher,
        Double discountAmount,
        Double originalTotal,
        Double newTotal,
        String message
) {
}
