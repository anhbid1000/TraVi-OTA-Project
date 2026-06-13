package com.ota.travi.dto.response;

import java.time.LocalDateTime;

public record MilestoneRewardResponse(
        Integer milestone,
        Integer bookingCount,
        String reward,
        LocalDateTime grantedAt
) {
}
