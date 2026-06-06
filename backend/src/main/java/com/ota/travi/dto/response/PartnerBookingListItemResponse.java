package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiDon;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PartnerBookingListItemResponse(
        String id,
        String maDon,
        String serviceType,
        String tenTaiSan,
        String tenNguoiDat,
        String sdtNguoiDat,
        LocalDateTime ngayTao,
        LocalDateTime ngayBatDau,
        LocalDateTime ngayKetThuc,
        LocalDate ngayCheckIn,
        LocalDate ngayCheckOut,
        Integer soKhach,
        Double tongTienThanhToan,
        Double tienCoc,
        TrangThaiDon trangThai
) {
}
