package com.ota.travi.dto.response;

public record PartnerRestaurantDashboardKpiResponse(
        Double tongDoanhThu,
        Double tyLeTangTruongDoanhThu,
        Double tyLeLapDayBan,
        Integer soBanDangPhucVu,
        Integer tongSoBanKhaDung,
        Double giaTriDonTrungBinh,
        Double tyLeTangTruongGiaTriDon,
        Integer tongLuotKhach,
        Integer chenhLechLuotKhach
) {
}
