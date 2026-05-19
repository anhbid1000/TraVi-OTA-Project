package com.traviota.booking.controller;

import com.traviota.booking.domain.dto.request.BookingRequest;
import com.traviota.booking.domain.entities.DonDatCho;
import com.traviota.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // POST /api/v1/bookings: Tiếp nhận gói hàng từ khách và chuyển cho Service xử lý
    @PostMapping
    public ResponseEntity<DonDatCho> createBooking(@Valid @RequestBody BookingRequest request) {
        DonDatCho newBooking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBooking);
    }
}