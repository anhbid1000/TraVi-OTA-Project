package com.ota.travi.dto.response;

import java.util.List;

public record UserAiRecommendationResponse(
        String profileSummary,
        List<String> profileSignals,
        List<AiRecommendationItemResponse> recommendations
) {
}
