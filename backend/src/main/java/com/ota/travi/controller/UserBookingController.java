package com.ota.travi.controller;

import com.ota.travi.dto.request.CancelBookingRequest;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.BookingService;
import com.ota.travi.service.CancelBookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ota.travi.constant.ApiEndpoints.USER_BOOKINGS;

/**
 * Controller hỗ trợ trang MyBookingsV2
 * Endpoint: /api/v1/user/bookings
 */
@RestController
@RequestMapping(USER_BOOKINGS)
public class UserBookingController {

    private final BookingService bookingService;
    private final CancelBookingService cancelBookingService;

    public UserBookingController(BookingService bookingService, CancelBookingService cancelBookingService) {
        this.bookingService = bookingService;
        this.cancelBookingService = cancelBookingService;
    }

    @GetMapping
    public ResponseEntity<?> getMyBookings() {
        try {
            String customerId = getCurrentUserId();
            return ResponseEntity.ok(bookingService.getCustomerBookings(customerId));
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMyBookingDetail(@PathVariable("id") String bookingId) {
        try {
            String customerId = getCurrentUserId();
            return ResponseEntity.ok(bookingService.getCustomerBookingDetail(customerId, bookingId));
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelMyBooking(
            @PathVariable("id") String bookingId,
            @Valid @RequestBody CancelBookingRequest request
    ) {
        try {
            String customerId = getCurrentUserId();
            return ResponseEntity.ok(cancelBookingService.cancelBooking(bookingId, customerId, request));
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("Không thể xác thực người dùng");
    }
}
