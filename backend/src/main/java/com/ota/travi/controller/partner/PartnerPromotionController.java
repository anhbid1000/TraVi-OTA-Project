package com.ota.travi.controller.partner;

import com.ota.travi.constant.ApiEndpoints;
import com.ota.travi.dto.request.PromotionCreateRequest;
import com.ota.travi.dto.request.VoucherCreateRequest;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOI_TAC')")
public class PartnerPromotionController {

    private final PromotionService promotionService;

    @PostMapping(ApiEndpoints.PARTNER_PROMOTIONS + "/direct")
    public ResponseEntity<PromotionResponse> createDirectPromotion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PromotionCreateRequest request
    ) {
        PromotionResponse response = promotionService.createDirectPromotion(request, currentPartnerId(userDetails), false);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(ApiEndpoints.PARTNER_PROMOTIONS + "/voucher")
    public ResponseEntity<PromotionResponse> createVoucher(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody VoucherCreateRequest request
    ) {
        PromotionResponse response = promotionService.createVoucher(request, currentPartnerId(userDetails), false);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(ApiEndpoints.PARTNER_PROMOTIONS)
    public ResponseEntity<List<PromotionResponse>> getPartnerPromotions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<PromotionResponse> response = promotionService.getPromotionsByPartner(currentPartnerId(userDetails));
        return ResponseEntity.ok(response);
    }

    @PatchMapping(ApiEndpoints.PARTNER_PROMOTIONS_PAUSE)
    public ResponseEntity<PromotionResponse> pausePromotion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        PromotionResponse response = promotionService.pausePromotion(id, currentPartnerId(userDetails), false);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(ApiEndpoints.PARTNER_PROMOTIONS_RESUME)
    public ResponseEntity<PromotionResponse> resumePromotion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        PromotionResponse response = promotionService.resumePromotion(id, currentPartnerId(userDetails), false);
        return ResponseEntity.ok(response);
    }

    private String currentPartnerId(CustomUserDetails userDetails) {
        return userDetails.getUser().getId();
    }
}
