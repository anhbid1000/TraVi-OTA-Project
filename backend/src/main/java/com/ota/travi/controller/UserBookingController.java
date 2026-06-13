package com.ota.travi.controller;

import com.ota.travi.dto.request.CancelBookingRequest;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.BookingService;
import com.ota.travi.service.BookingVoucherService;
import com.ota.travi.service.CancelBookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ota.travi.constant.ApiEndpoints.USER_BOOKINGS;

/**
 * Controller ho tro trang MyBookingsV2.
 * Endpoint: /api/v1/user/bookings
 */
@RestController
@RequestMapping(USER_BOOKINGS)
public class UserBookingController {

    private final BookingService bookingService;
    private final CancelBookingService cancelBookingService;
    private final BookingVoucherService bookingVoucherService;

    public UserBookingController(
            BookingService bookingService,
            CancelBookingService cancelBookingService,
            BookingVoucherService bookingVoucherService
    ) {
        this.bookingService = bookingService;
        this.cancelBookingService = cancelBookingService;
        this.bookingVoucherService = bookingVoucherService;
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

    @PostMapping("/{bookingId}/apply-voucher")
    public ResponseEntity<BookingVoucherService.VoucherCheckoutResponse> applyVoucherToBooking(
            @PathVariable String bookingId,
            @Valid @RequestBody ApplyVoucherRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String customerId = resolveCustomerId(userDetails);
        BookingVoucherService.VoucherCheckoutResponse response =
                bookingVoucherService.applyVoucherToBooking(bookingId, customerId, request.maVoucher());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{bookingId}/voucher")
    public ResponseEntity<Void> removeVoucherFromBooking(
            @PathVariable String bookingId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String customerId = resolveCustomerId(userDetails);
        bookingVoucherService.removeVoucherFromBooking(bookingId, customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{bookingId}/voucher-preview")
    public ResponseEntity<BookingVoucherService.VoucherPreviewResponse> previewVoucher(
            @PathVariable String bookingId,
            @RequestParam("maVoucher") String maVoucher,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String customerId = resolveCustomerId(userDetails);
        BookingVoucherService.VoucherPreviewResponse response =
                bookingVoucherService.previewVoucherDiscount(bookingId, customerId, maVoucher);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}/available-vouchers")
    public ResponseEntity<List<BookingVoucherService.VoucherPreviewResponse>> getAvailableVouchers(
            @PathVariable String bookingId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String customerId = resolveCustomerId(userDetails);
        List<BookingVoucherService.VoucherPreviewResponse> response =
                bookingVoucherService.getAvailableVouchersForBooking(bookingId, customerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("Khong the xac thuc nguoi dung");
    }

    private String resolveCustomerId(CustomUserDetails userDetails) {
        if (userDetails != null && userDetails.getUser() != null) {
            return userDetails.getUser().getId();
        }
        return getCurrentUserId();
    }

    public record ApplyVoucherRequest(
            @NotBlank(message = "Ma voucher khong duoc de trong")
            String maVoucher
    ) {
    }
}
