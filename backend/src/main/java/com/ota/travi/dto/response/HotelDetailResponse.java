package com.ota.travi.dto.response;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public record HotelDetailResponse(
        String id,
        String tenKhachSan,
        String moTa,
        String diaChi,
        String thanhPho,
        String quanHuyen,
        String phuongXa,
        Double kinhDo,
        Double viDo,
        Integer hangSao,
        LocalTime gioNhanPhong,
        LocalTime gioTraPhong,
        ChinhSachResponse chinhSach,
        Double diemDanhGiaTrungBinh,
        Integer soLuongDanhGia,
        List<AnhResponse> images,
        Set<TienIchKhachSanResponse> amenities,
        List<RoomAvailabilityResponse> availableRooms
) {
}


