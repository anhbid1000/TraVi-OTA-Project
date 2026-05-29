package com.ota.travi.dto.response;

import com.ota.travi.enums.VaiTroTinNhan;

import java.time.LocalDateTime;

/**
 * Response một tin nhắn trong ticket khiếu nại.
 */
public record ComplaintMessageResponse(
        VaiTroTinNhan senderRole,
        String content,
        LocalDateTime createdAt
) {
}
