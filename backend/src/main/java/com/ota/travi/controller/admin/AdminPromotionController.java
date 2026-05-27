package com.ota.travi.controller.admin;

import com.ota.travi.constant.ApiEndpoints;
import com.ota.travi.dto.request.LoyaltyRuleRequest;
import com.ota.travi.dto.request.PromotionCreateRequest;
import com.ota.travi.dto.request.VoucherCreateRequest;
import com.ota.travi.dto.response.LoyaltyRuleResponse;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.service.LoyaltyRuleService;
import com.ota.travi.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping // Xóa mapping prefix vì đã dùng ApiEndpoints constant
public class AdminPromotionController {

    private final PromotionService promotionService;
    private final LoyaltyRuleService loyaltyRuleService;

    @PostMapping(ApiEndpoints.ADMIN_PROMOTIONS + "/direct")
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN')")
    public ResponseEntity<PromotionResponse> createDirectPromotion(@Valid @RequestBody PromotionCreateRequest request) {
        String adminId = "mock-admin-id"; // Todo: replace
        PromotionResponse response = promotionService.createDirectPromotion(request, adminId, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(ApiEndpoints.ADMIN_PROMOTIONS + "/voucher")
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN')")
    public ResponseEntity<PromotionResponse> createVoucher(@Valid @RequestBody VoucherCreateRequest request) {
        String adminId = "mock-admin-id"; // Todo: replace
        PromotionResponse response = promotionService.createVoucher(request, adminId, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(ApiEndpoints.ADMIN_PROMOTIONS)
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN')")
    public ResponseEntity<List<PromotionResponse>> getAllPromotions() {
        List<PromotionResponse> response = promotionService.getAllPromotions();
        return ResponseEntity.ok(response);
    }

    @PatchMapping(ApiEndpoints.ADMIN_PROMOTIONS_PAUSE)
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN')")
    public ResponseEntity<PromotionResponse> pausePromotion(@PathVariable Long id) {
        String adminId = "mock-admin-id"; // Todo: replace
        PromotionResponse response = promotionService.pausePromotion(id, adminId, true);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(ApiEndpoints.ADMIN_PROMOTIONS_RESUME)
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN')")
    public ResponseEntity<PromotionResponse> resumePromotion(@PathVariable Long id) {
        String adminId = "mock-admin-id"; // Todo: replace
        PromotionResponse response = promotionService.resumePromotion(id, adminId, true);
        return ResponseEntity.ok(response);
    }

    @PutMapping(ApiEndpoints.ADMIN_LOYALTY_RULES)
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN')")
    public ResponseEntity<LoyaltyRuleResponse> updateLoyaltyRule(@Valid @RequestBody LoyaltyRuleRequest request) {
        LoyaltyRuleResponse response = loyaltyRuleService.updateLoyaltyRule(request);
        return ResponseEntity.ok(response);
    }
}
