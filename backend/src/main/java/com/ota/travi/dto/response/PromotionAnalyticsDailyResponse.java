package com.ota.travi.dto.response;

import java.time.LocalDate;

public record PromotionAnalyticsDailyResponse(
        LocalDate ngay,
        Integer usageCount,
        Integer bookingCount,
        Double generatedRevenue,
        Double discountCost,
        Double conversionRate
) {
}
