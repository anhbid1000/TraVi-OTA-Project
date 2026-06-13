package com.ota.travi.dto.response;

import java.time.LocalDateTime;

public record PointHistoryResponse(
        Long id,
        String customerId,
        Integer soDiemThayDoi,
        String loaiGiaoDichDiem,
        Integer diemTruocGiaoDich,
        Integer diemSauGiaoDich,
        String bookingId,
        Long voucherId,
        String ghiChu,
        LocalDateTime createdAt
) {
}
