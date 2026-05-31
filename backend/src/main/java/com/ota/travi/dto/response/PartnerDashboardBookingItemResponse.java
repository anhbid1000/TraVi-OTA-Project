package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiDon;

import java.time.LocalDate;

public record PartnerDashboardBookingItemResponse(
        String id,
        String maDon,
        String tenKhach,
        LocalDate ngayNhanPhong,
        LocalDate ngayTraPhong,
        String loaiPhong,
        Double tongTien,
        TrangThaiDon trangThai
) {
}
