package com.ota.travi.dto.request;

import com.ota.travi.enums.TrangThaiDon;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PartnerUpdateBookingStatusRequest(
        @NotNull(message = "targetStatus khong duoc de trong")
        TrangThaiDon targetStatus,

        @Size(max = 500, message = "Ly do khong duoc vuot qua 500 ky tu")
        String reason
) {
}
