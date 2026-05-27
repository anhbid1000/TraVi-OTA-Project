package com.ota.travi.dto.response;

import com.ota.travi.enums.LoaiGiaoDichDiem;

import java.time.LocalDateTime;

public record PointHistoryResponse(
        Long id,
        String khachHangId,
        Integer soDiemThayDoi,
        LoaiGiaoDichDiem loaiGiaoDichDiem,
        Integer diemTruocGiaoDich,
        Integer diemSauGiaoDich,
        Long bookingId,
        Long voucherId,
        String ghiChu,
        LocalDateTime createdAt
) {}
