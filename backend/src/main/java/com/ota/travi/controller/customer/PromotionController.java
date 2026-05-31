package com.ota.travi.controller.customer;

import com.ota.travi.constant.ApiEndpoints;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.dto.response.VoucherApplyResponse;
import com.ota.travi.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping(ApiEndpoints.CUSTOMER_PROMOTIONS)
    public ResponseEntity<List<PromotionResponse>> getAllPromotions() {
        List<PromotionResponse> responses = promotionService.getAllPromotions();
        return ResponseEntity.ok(responses);
    }

    @PostMapping(ApiEndpoints.CUSTOMER_PROMOTIONS_APPLY)
    public ResponseEntity<VoucherApplyResponse> applyVoucher(
            @RequestParam String voucherCode,
            @RequestParam BigDecimal originalTotal
    ) {
        VoucherApplyResponse response = promotionService.applyVoucher(voucherCode, originalTotal);
        return ResponseEntity.ok(response);
    }
}
