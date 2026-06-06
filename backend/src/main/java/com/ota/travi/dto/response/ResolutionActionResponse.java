package com.ota.travi.dto.response;

import com.ota.travi.enums.ComplaintResolutionActionStatus;
import com.ota.travi.enums.ComplaintResolutionActionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ResolutionActionResponse(
        String id,
        String complaintId,
        ComplaintResolutionActionType actionType,
        String tieuDe,
        String moTa,
        BigDecimal amount,
        String currency,
        String voucherCode,
        Integer discountPercent,
        ComplaintResolutionActionStatus status,
        String proposedByPartnerName,
        String customerResponseNote,
        String partnerCompletionNote,
        LocalDateTime proposedAt,
        LocalDateTime customerRespondedAt,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<AttachmentResponse> attachments
) {
}
