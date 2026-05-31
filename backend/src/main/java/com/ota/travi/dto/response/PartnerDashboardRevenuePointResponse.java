package com.ota.travi.dto.response;

import java.time.LocalDate;

public record PartnerDashboardRevenuePointResponse(
        LocalDate ngay,
        Double doanhThu
) {
}
