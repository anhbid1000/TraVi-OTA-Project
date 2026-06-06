package com.ota.travi.dto.response;

import java.time.LocalDateTime;

public record PartnerAiInsightItemResponse(
        String id,
        String insightType,
        String severity,
        String title,
        String message,
        String recommendedAction,
        String metricName,
        Double metricValue,
        LocalDateTime createdAt
) {
}
