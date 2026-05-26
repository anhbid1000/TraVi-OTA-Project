package com.ota.travi.dto.request;

import com.ota.travi.enums.ApprovalDecisionStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApprovalStatusRequest(
        @NotNull(message = "Trang thai kiem duyet khong duoc de trong")
        ApprovalDecisionStatus status,

        @Size(max = 500, message = "Ly do khong duoc vuot qua 500 ky tu")
        String reason
) {
}
