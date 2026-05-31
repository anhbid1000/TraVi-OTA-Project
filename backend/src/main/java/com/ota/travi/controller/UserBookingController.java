package com.ota.travi.controller;

import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ota.travi.constant.ApiEndpoints.USER_PREFIX;

/**
 * Controller hỗ trợ trang MyBookingsV2
 * Endpoint: /api/v1/user/bookings
 */
@RestController
@RequestMapping(USER_PREFIX + "/bookings")
public class UserBookingController {

    private final BookingService bookingService;

    public UserBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
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

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("Không thể xác thực người dùng");
    }
}
