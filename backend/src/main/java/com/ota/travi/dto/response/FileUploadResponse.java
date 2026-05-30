package com.ota.travi.dto.response;

public record FileUploadResponse(
        String fileName,
        String url,
        String contentType,
        long size
) {
}
