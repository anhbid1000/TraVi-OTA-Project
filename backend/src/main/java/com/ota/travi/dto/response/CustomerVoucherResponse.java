package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiCustomerVoucher;

import java.time.LocalDateTime;

public record CustomerVoucherResponse(
        Long id,
        Long voucherId,
        String khachHangId,
        String maVoucher,
        String maVoucherCaNhan,
        String tenUuDai,
        TrangThaiCustomerVoucher trangThai,
        LocalDateTime issuedAt,
        LocalDateTime usedAt,
        LocalDateTime expiredAt,
        Long bookingId
) {}
