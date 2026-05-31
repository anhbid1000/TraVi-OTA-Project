package com.ota.travi.booking.controller;

import com.ota.travi.booking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Khách bấm thanh toán sẽ gọi vào đây để lấy Link
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestParam Long bookingId) {
        return ResponseEntity.ok(paymentService.createCheckoutUrl(bookingId));
    }

    // Cổng ngân hàng gọi ngầm về đây để báo trả tiền xong
    @PostMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam Long bookingId,
            @RequestParam String maGiaoDichNganHang,
            @RequestParam String phuongThuc,
            @RequestParam BigDecimal soTien) {
        return ResponseEntity.ok(paymentService.handleWebhook(bookingId, maGiaoDichNganHang, phuongThuc, soTien));
    }
}