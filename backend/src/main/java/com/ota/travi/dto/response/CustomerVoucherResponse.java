package com.ota.travi.dto.response;

import java.time.LocalDateTime;

public record CustomerVoucherResponse(
        Long id,
        Long voucherId,
        String customerId,
        String maVoucher,
        String maVoucherCaNhan,
        String tenUuDai,
        String trangThai,
        String sourceType,
        LocalDateTime issuedAt,
        LocalDateTime reservedAt,
        LocalDateTime reserveExpiresAt,
        LocalDateTime usedAt,
        LocalDateTime expiredAt,
        Long bookingId,
        Boolean isReserveExpired
) {
    public static Boolean calculateIsReserveExpired(LocalDateTime reserveExpiresAt) {
        if (reserveExpiresAt == null) {
            return false;
        }
        return reserveExpiresAt.isBefore(LocalDateTime.now());
    }
}
