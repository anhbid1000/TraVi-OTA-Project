package com.ota.travi.dto.response;

public record HotelBookingRoomResponse(
        String roomId,
        String tenPhong,
        Integer soLuong,
        Double donGia,
        Integer soDem,
        Double thanhTien
) {
}
