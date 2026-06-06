package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiDon;

import java.time.LocalDateTime;
import java.util.List;

public record RestaurantBookingResponse(
        String id,
        String maDon,
        String restaurantId,
        String tenNhaHang,
        LocalDateTime ngayGioBatDau,
        LocalDateTime ngayGioKetThuc,
        Integer soNguoi,
        Double tienCoc,
        Double tongTienThanhToan,
        TrangThaiDon trangThaiDon,
        LocalDateTime paymentExpiredAt,
        Long paymentExpiresInSeconds,
        List<RestaurantAssignedTableResponse> tables
) {
}
