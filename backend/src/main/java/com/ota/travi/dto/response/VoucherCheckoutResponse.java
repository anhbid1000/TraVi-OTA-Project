package com.ota.travi.dto.response;

import java.math.BigDecimal;

public record VoucherCheckoutResponse(
        Long bookingId,
        Long voucherId,
        BigDecimal discountAmount,
        BigDecimal newTotal,
        BigDecimal originalTotal,
        String message
) {
}
