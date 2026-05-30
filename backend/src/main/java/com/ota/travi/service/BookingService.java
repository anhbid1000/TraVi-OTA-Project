package com.ota.travi.service;

import com.ota.travi.dto.response.UserBookingResponse;

import java.util.List;

/**
 * Service cho API My Bookings (Module 5 Phase 3).
 */
public interface BookingService {
    List<UserBookingResponse> getCustomerBookings(String customerId);
}
