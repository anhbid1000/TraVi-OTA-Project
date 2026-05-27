package com.ota.travi.dto.response;

import java.math.BigDecimal;

public record VoucherApplyResponse(
        String voucherCode,
        BigDecimal discountAmount,
        BigDecimal originalTotal,
        BigDecimal newTotal,
        String message
) {}
