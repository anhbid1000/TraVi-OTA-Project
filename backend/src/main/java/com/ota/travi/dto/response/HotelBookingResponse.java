package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiDon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record HotelBookingResponse(
        String id,
        String maDon,
        String hotelId,
        String tenKhachSan,
        LocalDate ngayCheckIn,
        LocalDate ngayCheckOut,
        Integer soDem,
        Integer soKhach,
        LocalTime gioNhanPhongDuKien,
        Double tongTienGoc,
        Double tienKhuyenMai,
        Double tongTienThanhToan,
        TrangThaiDon trangThaiDon,
        LocalDateTime paymentExpiredAt,
        Long paymentExpiresInSeconds,
        List<HotelBookingRoomResponse> rooms
) {
}
