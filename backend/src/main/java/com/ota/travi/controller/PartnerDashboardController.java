package com.ota.travi.controller;

import com.ota.travi.dto.response.PartnerDashboardOverviewResponse;
import com.ota.travi.dto.response.PartnerRestaurantDashboardOverviewResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.PartnerDashboardService;
import com.ota.travi.service.PartnerRestaurantDashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ota.travi.constant.ApiEndpoints.PARTNER_DASHBOARD;

@RestController
@PreAuthorize("hasRole('DOI_TAC')")
public class PartnerDashboardController {
    private final PartnerDashboardService partnerDashboardService;
    private final PartnerRestaurantDashboardService partnerRestaurantDashboardService;

    public PartnerDashboardController(
            PartnerDashboardService partnerDashboardService,
            PartnerRestaurantDashboardService partnerRestaurantDashboardService
    ) {
        this.partnerDashboardService = partnerDashboardService;
        this.partnerRestaurantDashboardService = partnerRestaurantDashboardService;
    }

    @GetMapping(PARTNER_DASHBOARD + "/overview")
    public ResponseEntity<?> getDashboardOverview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "days", required = false) Integer days
    ) {
        PartnerDashboardOverviewResponse response = partnerDashboardService.getOverview(
                userDetails.getUser().getId(),
                days
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(PARTNER_DASHBOARD + "/restaurant-overview")
    public ResponseEntity<?> getRestaurantDashboardOverview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "period", required = false) String period
    ) {
        PartnerRestaurantDashboardOverviewResponse response = partnerRestaurantDashboardService.getOverview(
                userDetails.getUser().getId(),
                period
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
