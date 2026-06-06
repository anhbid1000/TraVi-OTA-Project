package com.ota.travi.dto.response;

import java.util.List;

public record LoyaltySummaryResponse(
        String customerId,
        Integer currentPoints,
        Double totalSpending,
        String currentTier,
        String nextTier,
        Double requiredSpendingForNext,
        Double remainingSpending,
        Double progressPercent,
        List<PointHistoryResponse> pointHistory,
        List<CustomerVoucherResponse> customerVouchers
) {
}
