package com.ota.travi.dto.response;

import java.time.LocalDateTime;

public record PointHistoryResponse(
        Long id,
        Long customerId,
        Integer soDiemThayDoi,
        String loaiGiaoDichDiem,
        Integer diemTruocGiaoDich,
        Integer diemSauGiaoDich,
        Long bookingId,
        Long voucherId,
        String ghiChu,
        LocalDateTime createdAt
) {
}
