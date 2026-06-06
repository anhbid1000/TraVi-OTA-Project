package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ConfirmHotelBookingPaymentRequest(
        @NotBlank(message = "bookingId không được để trống")
        String bookingId,

        String paymentRef
) {
}
