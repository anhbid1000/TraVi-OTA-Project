package com.ota.travi.dto.request;

import com.ota.travi.enums.ComplaintResolutionActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ResolutionActionCreateRequest(
        @NotNull(message = "Loại hành động không được rỗng")
        ComplaintResolutionActionType actionType,

        @NotBlank(message = "Tiêu đề không được rỗng")
        @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
        String tieuDe,

        @NotBlank(message = "Mô tả không được rỗng")
        String moTa,

        BigDecimal amount,
        String currency,
        String voucherCode,
        Integer discountPercent
) {
}
