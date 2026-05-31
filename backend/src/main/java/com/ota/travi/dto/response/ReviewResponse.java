package com.ota.travi.dto.response;

import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiDanhGia;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response review công khai.
 */
public record ReviewResponse(
        String id,
        String customerName,
        String businessProfileId,
        LoaiDichVu serviceType,
        Integer rating,
        String content,
        TrangThaiDanhGia status,
        LocalDateTime createdAt,
        List<AttachmentResponse> attachments,
        List<ReviewAspectScoreResponse> aspectScores,
        ReviewReplyResponse partnerReply
) {
}
