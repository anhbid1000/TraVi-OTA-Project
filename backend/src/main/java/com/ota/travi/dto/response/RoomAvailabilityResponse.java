package com.ota.travi.dto.response;

import com.ota.travi.enums.TienIchPhong;

import java.util.List;
import java.util.Set;

public record RoomAvailabilityResponse(
        String roomId,
        String tenPhong,
        String loaiPhong,
        String moTa,
        Float dienTich,
        Integer soKhachToiDa,
        Integer soGiuong,
        Double giaCoBan,
        Integer soLuongConTrong,
        List<AnhResponse> images,
        Set<TienIchPhong> amenities
) {
}


