package com.ota.travi.dto.response;

/**
 * DTO trả về metadata file đính kèm cho frontend.
 */
public record AttachmentResponse(
        String id,
        String fileUrl,
        String fileName,
        String fileType,
        String mimeType,
        Long fileSize
) {}
