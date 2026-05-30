package com.ota.travi.dto.response;

import com.ota.travi.enums.VaiTroTinNhan;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response một tin nhắn trong ticket khiếu nại.
 */
public record ComplaintMessageResponse(
        String id,
        VaiTroTinNhan senderRole,
        String content,
        LocalDateTime createdAt,
        List<AttachmentResponse> attachments
) {
}
