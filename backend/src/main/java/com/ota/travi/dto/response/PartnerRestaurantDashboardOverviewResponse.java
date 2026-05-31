package com.ota.travi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PartnerRestaurantDashboardOverviewResponse(
        String businessProfileId,
        String restaurantId,
        String tenNhaHang,
        String period,
        LocalDateTime tuThoiDiem,
        LocalDateTime denThoiDiem,
        PartnerRestaurantDashboardKpiResponse tongQuan,
        List<PartnerRestaurantDashboardRevenuePointResponse> doanhThuTheoGio,
        List<PartnerRestaurantDashboardPeakHourResponse> khungGioCaoDiem,
        List<PartnerRestaurantDashboardRecentOrderResponse> donGanDay,
        List<PartnerRestaurantDashboardTopDishResponse> monBanChay
) {
}
