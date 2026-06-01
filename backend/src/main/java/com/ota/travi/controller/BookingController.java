package com.ota.travi.controller;

import com.ota.travi.dto.response.BookingSearchResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ota.travi.constant.ApiEndpoints.USER_BOOKING_HISTORY;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_BOOKING_SEARCH;

@RestController
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Legacy endpoint retained for backward compatibility with old search page.
    @GetMapping(USER_BOOKING_HISTORY)
    public ResponseEntity<?> getBookingHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "type", required = false, defaultValue = "all") String type
    ) {
        try {
            String username = userDetails.getUsername();
            List<BookingSearchResponse> history = bookingService.getBookingHistory(username, type);
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi lấy lịch sử đặt chỗ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(PUBLIC_BOOKING_SEARCH)
    public ResponseEntity<?> lookupBookingPublicly(
            @RequestParam("maDon") String maDon,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone
    ) {
        try {
            BookingSearchResponse booking = bookingService.lookupBookingPublicly(maDon, email, phone);
            return new ResponseEntity<>(booking, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi tra cứu đơn đặt chỗ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
