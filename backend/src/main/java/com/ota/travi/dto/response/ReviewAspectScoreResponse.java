package com.ota.travi.dto.response;

import com.ota.travi.enums.ReviewAspectType;

/**
 * Response của từng điểm đánh giá khía cạnh.
 */
public record ReviewAspectScoreResponse(
        ReviewAspectType aspect,
        Integer score
) {}
