package com.ota.travi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoyaltyRuleRequest(
        @NotNull(message = "Số tiền đổi 1 điểm không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền đổi 1 điểm phải lớn hơn 0")
        BigDecimal moneyPerPoint,

        @NotNull(message = "Ngưỡng hạng bạc không được để trống")
        @DecimalMin(value = "0.0", message = "Ngưỡng hạng bạc không được âm")
        BigDecimal silverThreshold,

        @NotNull(message = "Ngưỡng hạng vàng không được để trống")
        @DecimalMin(value = "0.0", message = "Ngưỡng hạng vàng không được âm")
        BigDecimal goldThreshold,

        @NotNull(message = "Ngưỡng hạng kim cương không được để trống")
        @DecimalMin(value = "0.0", message = "Ngưỡng hạng kim cương không được âm")
        BigDecimal diamondThreshold,

        Boolean isActive
) {}
