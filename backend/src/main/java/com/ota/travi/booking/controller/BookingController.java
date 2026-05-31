package com.ota.travi.booking.controller;

import com.ota.travi.booking.domain.dto.request.BookingRequest;
import com.ota.travi.booking.domain.entities.DonDatCho;
import com.ota.travi.booking.service.BookingService;
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
// === CỔNG API CHO GIAI ĐOẠN 3: PARTNER APIs ===

    // API 3.1: Lấy danh sách đơn hàng dành cho Đối tác (Có thể lọc theo trạng thái)
    @GetMapping("/partner/list")
    public ResponseEntity<java.util.List<DonDatCho>> getPartnerBookings(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(bookingService.getPartnerBookings(status));
    }

    // API 3.2a: Đối tác bấm Xác nhận khách đã đến nhận phòng/bàn (Check-in)
    @PutMapping("/partner/{id}/check-in")
    public ResponseEntity<DonDatCho> partnerCheckIn(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.checkIn(id));
    }

    // API 3.2b: Đối tác bấm Xác nhận khách trả phòng/bàn đi về (Check-out)
    @PutMapping("/partner/{id}/check-out")
    public ResponseEntity<DonDatCho> partnerCheckOut(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.checkOut(id));
    }

    // API 3.2c: Đối tác bấm phạt do khách quá giờ không đến (No-show)
    @PutMapping("/partner/{id}/no-show")
    public ResponseEntity<DonDatCho> partnerNoShow(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.noShow(id));
    }
}