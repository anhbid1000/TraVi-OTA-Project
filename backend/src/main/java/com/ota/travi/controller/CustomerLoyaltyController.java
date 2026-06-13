package com.ota.travi.controller;

import com.ota.travi.dto.request.ExchangeVoucherRequest;
import com.ota.travi.dto.response.CustomerVoucherResponse;
import com.ota.travi.dto.response.ExchangeVoucherResponse;
import com.ota.travi.dto.response.LoyaltyProgressResponse;
import com.ota.travi.dto.response.LoyaltySummaryResponse;
import com.ota.travi.dto.response.PointHistoryResponse;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.CustomerLoyaltyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ota.travi.constant.ApiEndpoints.USER_PREFIX;

@RestController
@RequestMapping(USER_PREFIX + "/loyalty")
@PreAuthorize("hasRole('KHACH_HANG')")
public class CustomerLoyaltyController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerLoyaltyController.class);
    private final CustomerLoyaltyService customerLoyaltyService;

    public CustomerLoyaltyController(CustomerLoyaltyService customerLoyaltyService) {
        this.customerLoyaltyService = customerLoyaltyService;
    }

    @GetMapping("/summary")
    public ResponseEntity<LoyaltySummaryResponse> getLoyaltySummary(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info("Fetching loyalty summary for customer: {}", getCurrentCustomerId(userDetails));
        String customerId = getCurrentCustomerId(userDetails);
        LoyaltySummaryResponse response = customerLoyaltyService.getLoyaltySummary(customerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/progress")
    public ResponseEntity<LoyaltyProgressResponse> getLoyaltyProgress(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String customerId = getCurrentCustomerId(userDetails);
        return ResponseEntity.ok(customerLoyaltyService.getLoyaltyProgress(customerId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<PointHistoryResponse>> getPointHistory(
            @RequestParam(name = "limit", required = false) Integer limit,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String customerId = getCurrentCustomerId(userDetails);
        return ResponseEntity.ok(customerLoyaltyService.getPointHistory(customerId, limit));
    }

    @GetMapping("/wallet/vouchers")
    public ResponseEntity<List<CustomerVoucherResponse>> getWalletVouchers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String customerId = getCurrentCustomerId(userDetails);
        return ResponseEntity.ok(customerLoyaltyService.getWalletVouchers(customerId));
    }

    @GetMapping("/exchangeable-vouchers")
    public ResponseEntity<List<PromotionResponse>> getExchangeableVouchers() {
        logger.info("Fetching exchangeable vouchers");
        List<PromotionResponse> responses = customerLoyaltyService.getExchangeableVouchers();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/exchange")
    public ResponseEntity<ExchangeVoucherResponse> exchangeVoucher(
            @Valid @RequestBody ExchangeVoucherRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info("Exchanging voucher {} for customer: {}", request.voucherId(), getCurrentCustomerId(userDetails));
        String customerId = getCurrentCustomerId(userDetails);
        ExchangeVoucherResponse response = customerLoyaltyService.exchangeVoucher(customerId, request.voucherId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String getCurrentCustomerId(CustomUserDetails userDetails) {
        return userDetails.getUser().getId();
    }
}
