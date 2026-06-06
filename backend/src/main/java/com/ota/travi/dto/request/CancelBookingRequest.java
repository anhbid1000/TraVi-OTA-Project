package com.ota.travi.dto.request;

import jakarta.validation.constraints.Size;

public record CancelBookingRequest(
        @Size(max = 500, message = "Lý do hủy không được vượt quá 500 ký tự")
        String reason
) {}
