package com.ota.travi.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record AiRecommendationFeedbackRequest(
        String idTaiSan,
        Boolean viewed,
        Boolean clicked,
        Boolean booked,
        @Min(1) @Max(5) Integer feedbackRating,
        @Size(max = 1000) String feedbackNote
) {
}
