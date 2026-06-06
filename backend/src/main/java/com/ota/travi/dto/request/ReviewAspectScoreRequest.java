package com.ota.travi.dto.request;

import com.ota.travi.enums.ReviewAspectType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Điểm đánh giá theo từng khía cạnh của review.
 */
public record ReviewAspectScoreRequest(
        @NotNull(message = "Khía cạnh đánh giá không được để trống")
        ReviewAspectType aspect,

        @NotNull(message = "Điểm khía cạnh không được để trống")
        @Min(value = 1, message = "Điểm khía cạnh tối thiểu là 1")
        @Max(value = 5, message = "Điểm khía cạnh tối đa là 5")
        Integer score
) {}
