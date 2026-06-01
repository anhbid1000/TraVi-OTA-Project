package com.ota.travi.controller;

import com.ota.travi.dto.request.PromotionCreateRequest;
import com.ota.travi.dto.request.VoucherCreateRequest;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.PromotionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ota.travi.constant.ApiEndpoints.PARTNER_PROMOTIONS;

@RestController
@RequestMapping(PARTNER_PROMOTIONS)
@PreAuthorize("hasRole('DOI_TAC')")
public class PartnerPromotionController {

    private static final Logger logger = LoggerFactory.getLogger(PartnerPromotionController.class);
    private final PromotionService promotionService;

    public PartnerPromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping("/direct")
    public ResponseEntity<PromotionResponse> createDirectPromotion(
            @Valid @RequestBody PromotionCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info("Creating direct promotion for partner: {}", getCurrentPartnerId(userDetails));
        String partnerId = getCurrentPartnerId(userDetails);
        PromotionResponse response = promotionService.createDirectPromotion(request, partnerId, false);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/voucher")
    public ResponseEntity<PromotionResponse> createVoucher(
            @Valid @RequestBody VoucherCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info("Creating voucher for partner: {}", getCurrentPartnerId(userDetails));
        String partnerId = getCurrentPartnerId(userDetails);
        PromotionResponse response = promotionService.createVoucher(request, partnerId, false);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getPromotions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info("Fetching promotions for partner: {}", getCurrentPartnerId(userDetails));
        String partnerId = getCurrentPartnerId(userDetails);
        List<PromotionResponse> responses = promotionService.getPromotionsByPartner(partnerId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PatchMapping("/{id}/pause")
    public ResponseEntity<PromotionResponse> pausePromotion(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info("Pausing promotion {} for partner: {}", id, getCurrentPartnerId(userDetails));
        String partnerId = getCurrentPartnerId(userDetails);
        PromotionResponse response = promotionService.pausePromotion(id, partnerId, false);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/resume")
    public ResponseEntity<PromotionResponse> resumePromotion(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info("Resuming promotion {} for partner: {}", id, getCurrentPartnerId(userDetails));
        String partnerId = getCurrentPartnerId(userDetails);
        PromotionResponse response = promotionService.resumePromotion(id, partnerId, false);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String getCurrentPartnerId(CustomUserDetails userDetails) {
        return userDetails.getUser().getId();
    }
}
