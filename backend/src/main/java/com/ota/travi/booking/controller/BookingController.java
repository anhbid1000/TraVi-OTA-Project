package com.ota.travi.booking.controller;

import com.ota.travi.booking.domain.dto.request.BookingRequest;
import com.ota.travi.booking.domain.entities.DonDatCho;
import com.ota.travi.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("bookingModuleController")
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<DonDatCho> createBooking(@Valid @RequestBody BookingRequest request) {
        DonDatCho newBooking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBooking);
    }

    @GetMapping("/partner/list")
    public ResponseEntity<java.util.List<DonDatCho>> getPartnerBookings(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(bookingService.getPartnerBookings(status));
    }

    @PutMapping("/partner/{id}/check-in")
    public ResponseEntity<DonDatCho> partnerCheckIn(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.checkIn(id));
    }

    @PutMapping("/partner/{id}/check-out")
    public ResponseEntity<DonDatCho> partnerCheckOut(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.checkOut(id));
    }

    @PutMapping("/partner/{id}/no-show")
    public ResponseEntity<DonDatCho> partnerNoShow(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.noShow(id));
    }
}
