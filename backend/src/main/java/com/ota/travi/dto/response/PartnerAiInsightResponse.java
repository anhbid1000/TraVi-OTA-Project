package com.ota.travi.dto.response;

import java.time.LocalDate;
import java.util.List;

public record PartnerAiInsightResponse(
        String partnerId,
        String businessProfileId,
        String businessName,
        String serviceType,
        LocalDate fromDate,
        LocalDate toDate,
        Integer healthScore,
        String profileSummary,
        Double revenue,
        Double revenueGrowthPercent,
        Integer bookings,
        Double bookingGrowthPercent,
        Double averageRating,
        Integer reviewCount,
        String modelVersion,
        List<PartnerAiInsightItemResponse> insights
) {
}
