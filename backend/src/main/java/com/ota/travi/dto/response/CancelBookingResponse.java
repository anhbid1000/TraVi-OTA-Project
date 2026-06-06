package com.ota.travi.dto.response;

public record CancelBookingResponse(
        boolean success,
        String message,
        Double refundAmount,
        String bookingId,
        String maDon
) {}
