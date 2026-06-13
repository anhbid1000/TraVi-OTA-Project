package com.ota.travi.dto.response;

public record LoyaltyProgressResponse(
        String currentTier,
        Double totalSpending,
        String nextTier,
        Double requiredSpending,
        Double remainingSpending,
        Double progressPercent
) {
}
