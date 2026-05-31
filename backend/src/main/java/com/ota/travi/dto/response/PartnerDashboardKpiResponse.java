package com.ota.travi.dto.response;

public record PartnerDashboardKpiResponse(
        Double tongDoanhThu,
        Double tyLeTangTruongDoanhThu,
        Double tyLeLapDay,
        Double tyLeTangTruongLapDay,
        Double giaTrungBinhMoiDem,
        Double tyLeTangTruongGiaTrungBinh,
        Integer tongLuotDat,
        Double tyLeTangTruongLuotDat
) {
}
