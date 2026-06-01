package com.ota.travi.dto.request;

import jakarta.validation.constraints.Size;

public record AiUserEventRequest(
        @Size(max = 50) String eventType,
        @Size(max = 50) String assetType,
        String assetId,
        @Size(max = 255) String keyword,
        @Size(max = 255) String city,
        @Size(max = 100) String source,
        @Size(max = 1000) String metadata
) {
}
