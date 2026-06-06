package com.ota.travi.dto.response;

import com.ota.travi.enums.LoaiDichVu;

import java.time.LocalDate;
import java.util.List;

public record PartnerDashboardOverviewResponse(
        String businessProfileId,
        String assetId,
        String tenCoSo,
        LoaiDichVu loaiDichVu,
        LocalDate tuNgay,
        LocalDate denNgay,
        PartnerDashboardKpiResponse tongQuan,
        List<PartnerDashboardRevenuePointResponse> doanhThuTheoNgay,
        PartnerDashboardAvailabilityResponse tinhTrangHomNay,
        List<PartnerDashboardBookingItemResponse> datChoGanDay,
        List<PartnerDashboardTopRoomResponse> topPhongNhuCau
) {
}
