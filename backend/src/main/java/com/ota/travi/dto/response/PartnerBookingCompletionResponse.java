package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiDon;

public record PartnerBookingCompletionResponse(
        String bookingId,
        String maDon,
        TrangThaiDon trangThai,
        boolean loyaltyRewardProcessed,
        String message
) {
}
