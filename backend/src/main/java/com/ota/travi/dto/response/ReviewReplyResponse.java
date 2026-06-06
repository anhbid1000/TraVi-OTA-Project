package com.ota.travi.dto.response;

import java.time.LocalDateTime;

/**
 * Response phản hồi của đối tác dưới review.
 */
public record ReviewReplyResponse(
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}


