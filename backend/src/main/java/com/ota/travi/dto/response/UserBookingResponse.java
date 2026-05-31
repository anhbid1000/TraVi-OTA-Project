package com.ota.travi.dto.response;

/**
 * DTO trả về cho API My Bookings (Module 5 Phase 3).
 */
public record UserBookingResponse(
        String id,
        String serviceName,
        String serviceType,
        String bookingId,
        String reservationId,
        String dateLabel,
        Double totalPrice,
        String status,
        Boolean reviewed,
        String thumbnailUrl
) {
}
