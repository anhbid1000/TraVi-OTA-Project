package com.ota.travi.controller.customer;

import com.ota.travi.constant.ApiEndpoints;
import com.ota.travi.dto.request.ExchangeVoucherRequest;
import com.ota.travi.dto.response.ExchangeVoucherResponse;
import com.ota.travi.dto.response.LoyaltySummaryResponse;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.CustomerLoyaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('KHACH_HANG')")
public class CustomerLoyaltyController {
    private final CustomerLoyaltyService customerLoyaltyService;

    @GetMapping(ApiEndpoints.CUSTOMER_LOYALTY)
    public ResponseEntity<LoyaltySummaryResponse> getLoyaltySummary(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(customerLoyaltyService.getLoyaltySummary(currentCustomerId(userDetails)));
    }

    @GetMapping(ApiEndpoints.CUSTOMER_LOYALTY_EXCHANGEABLE_VOUCHERS)
    public ResponseEntity<List<PromotionResponse>> getExchangeableVouchers() {
        return ResponseEntity.ok(customerLoyaltyService.getExchangeableVouchers());
    }

    @PostMapping(ApiEndpoints.CUSTOMER_LOYALTY_EXCHANGE)
    public ResponseEntity<ExchangeVoucherResponse> exchangeVoucher(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ExchangeVoucherRequest request
    ) {
        return ResponseEntity.ok(customerLoyaltyService.exchangeVoucher(currentCustomerId(userDetails), request.voucherId()));
    }

    private String currentCustomerId(CustomUserDetails userDetails) {
        return userDetails.getUser().getId();
    }
}
