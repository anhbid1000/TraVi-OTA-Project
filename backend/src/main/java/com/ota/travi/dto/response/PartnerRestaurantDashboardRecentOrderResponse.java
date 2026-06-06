package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiDon;

import java.time.LocalDateTime;

public record PartnerRestaurantDashboardRecentOrderResponse(
        String id,
        String maDon,
        String tenKhachHang,
        LocalDateTime thoiGian,
        String banSo,
        Double tongTien,
        TrangThaiDon trangThai
) {
}
