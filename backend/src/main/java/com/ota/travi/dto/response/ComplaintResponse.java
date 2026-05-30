package com.ota.travi.dto.response;

import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.MucDoKhieuNai;
import com.ota.travi.enums.TrangThaiKhieuNai;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response complaint dạng summary/detail.
 */
public record ComplaintResponse(
        String id,
        String title,
        LoaiDichVu serviceType,
        MucDoKhieuNai severity,
        TrangThaiKhieuNai status,
        boolean overdue,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<AttachmentResponse> attachments,
        List<ComplaintMessageResponse> messages
) {
}
