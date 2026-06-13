package com.ota.travi.dto.response;

public record VoucherPreviewResponse(
        Long voucherId,
        String maVoucher,
        String tenUuDai,
        String moTa,
        String loaiGiamGia,
        Double mucGiam,
        Double giaTriGiamToiDa,
        Double donHangToiThieu,
        Double estimatedDiscount,
        Boolean isEligible,
        String reason
) {
}
