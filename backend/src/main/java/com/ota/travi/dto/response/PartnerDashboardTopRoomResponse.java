package com.ota.travi.dto.response;

public record PartnerDashboardTopRoomResponse(
        String roomId,
        String tenPhong,
        String loaiPhong,
        Integer soLuotDat,
        Double giaTrungBinh,
        String anhDaiDienUrl
) {
}
