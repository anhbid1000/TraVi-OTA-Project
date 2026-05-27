package com.ota.travi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoyaltyRuleResponse(
        Long id,
        BigDecimal moneyPerPoint,
        BigDecimal silverThreshold,
        BigDecimal goldThreshold,
        BigDecimal diamondThreshold,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
