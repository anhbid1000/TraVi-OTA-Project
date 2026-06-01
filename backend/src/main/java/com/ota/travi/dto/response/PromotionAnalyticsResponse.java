package com.ota.travi.dto.response;

public record PromotionAnalyticsResponse(
        Long campaignId,
        Integer usageCount,
        Integer bookingCount,
        Double generatedRevenue,
        Double discountCost,
        Double conversionRate
) {
}
