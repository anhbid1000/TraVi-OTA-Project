package com.ota.travi.controller;

import com.ota.travi.dto.request.PromotionCreateRequest;
import com.ota.travi.dto.request.VoucherCreateRequest;
import com.ota.travi.dto.response.PromotionAnalyticsResponse;
import com.ota.travi.dto.response.PromotionAnalyticsDailyResponse;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.PromotionService;
import com.ota.travi.service.PromotionAnalyticsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.ota.travi.constant.ApiEndpoints.PARTNER_PROMOTIONS;

@RestController
@RequestMapping(PARTNER_PROMOTIONS)
@PreAuthorize("hasRole('DOI_TAC')")
public class PartnerPromotionController {

    private static final Logger logger = LoggerFactory.getLogger(PartnerPromotionController.class);
    private final PromotionService promotionService;
    private final PromotionAnalyticsService promotionAnalyticsService;

    public PartnerPromotionController(
            PromotionService promotionService,
            PromotionAnalyticsService promotionAnalyticsService
    ) {
        this.promotionService = promotionService;
        this.promotionAnalyticsService = promotionAnalyticsService;
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

    @GetMapping("/{id}/analytics")
    public ResponseEntity<PromotionAnalyticsResponse> getPromotionAnalytics(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info("Fetching analytics summary for campaign {} by partner {}", id, getCurrentPartnerId(userDetails));
        String partnerId = getCurrentPartnerId(userDetails);
        requirePartnerOwnsCampaign(partnerId, id);
        PromotionAnalyticsService.PromotionAnalyticsSummaryResponse summary =
                promotionAnalyticsService.getCampaignAnalyticsSummary(id);

        PromotionAnalyticsResponse response = new PromotionAnalyticsResponse(
                summary.getCampaignId(),
                summary.getTotalVoucherUsages() != null ? summary.getTotalVoucherUsages().intValue() : 0,
                summary.getTotalBookings() != null ? summary.getTotalBookings().intValue() : 0,
                summary.getTotalGeneratedRevenue() != null ? summary.getTotalGeneratedRevenue().doubleValue() : 0.0,
                summary.getTotalDiscountCost() != null ? summary.getTotalDiscountCost().doubleValue() : 0.0,
                summary.getConversionRate() != null ? summary.getConversionRate().doubleValue() : 0.0
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}/analytics/daily")
    public ResponseEntity<List<PromotionAnalyticsDailyResponse>> getPromotionAnalyticsDaily(
            @PathVariable Long id,
            @RequestParam(value = "fromDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info("Fetching analytics daily for campaign {} by partner {}", id, getCurrentPartnerId(userDetails));
        String partnerId = getCurrentPartnerId(userDetails);
        requirePartnerOwnsCampaign(partnerId, id);

        LocalDate effectiveToDate = toDate != null ? toDate : LocalDate.now();
        LocalDate effectiveFromDate = fromDate != null ? fromDate : effectiveToDate.minusDays(30);

        List<PromotionAnalyticsDailyResponse> responses = promotionAnalyticsService
                .getCampaignAnalyticsDaily(id, effectiveFromDate, effectiveToDate)
                .stream()
                .map(daily -> new PromotionAnalyticsDailyResponse(
                        daily.getDate(),
                        daily.getVoucherUsageCount(),
                        daily.getBookingCount(),
                        daily.getGeneratedRevenue() != null ? daily.getGeneratedRevenue().doubleValue() : 0.0,
                        daily.getDiscountCost() != null ? daily.getDiscountCost().doubleValue() : 0.0,
                        daily.getConversionRate() != null ? daily.getConversionRate().doubleValue() : 0.0
                ))
                .toList();

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    private String getCurrentPartnerId(CustomUserDetails userDetails) {
        return userDetails.getUser().getId();
    }

    private void requirePartnerOwnsCampaign(String partnerId, Long campaignId) {
        boolean owned = promotionService.getPromotionsByPartner(partnerId).stream()
                .anyMatch(p -> p.id().equals(campaignId));
        if (!owned) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Partner does not have access to this campaign"
            );
        }
    }
}
