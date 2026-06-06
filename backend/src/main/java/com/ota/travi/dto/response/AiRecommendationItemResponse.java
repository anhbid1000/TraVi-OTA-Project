package com.ota.travi.dto.response;

import java.util.List;

public record AiRecommendationItemResponse(
        String id,
        String type,
        String name,
        String city,
        String district,
        Double basePrice,
        Integer score,
        List<String> reasons
) {
}
