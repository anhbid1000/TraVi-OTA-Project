package com.ota.travi.dto.response;

import com.ota.travi.enums.ComplaintActivityType;
import com.ota.travi.enums.ComplaintActorRole;

import java.time.LocalDateTime;

public record ComplaintActivityResponse(
        String id,
        String complaintId,
        ComplaintActivityType activityType,
        String actorId,
        ComplaintActorRole actorRole,
        String summary,
        String metadata,
        LocalDateTime createdAt
) {
}
