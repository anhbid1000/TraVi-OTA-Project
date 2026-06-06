package com.ota.travi.dto.response;

import java.util.List;

public record ItineraryResponse(
    String city,
    Integer durationDays,
    String budgetLevel,
    List<DailyPlan> dailyPlans
) {
    public record DailyPlan(
        int day,
        List<Activity> activities
    ) {}

    public record Activity(
        String timeWindow,
        String description,
        String recommendedAssetId,
        String assetType, // HOTEL, RESTAURANT
        String assetName
    ) {}
}
