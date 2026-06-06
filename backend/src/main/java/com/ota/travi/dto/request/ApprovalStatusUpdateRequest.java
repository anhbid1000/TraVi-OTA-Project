package com.ota.travi.dto.request;

import com.ota.travi.enums.ApprovalDecisionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApprovalStatusUpdateRequest(
        @NotNull ApprovalDecisionStatus status,
        @Size(max = 500) String reason
) {
}

