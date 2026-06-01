package com.ota.travi.controller;

import com.ota.travi.dto.response.PartnerAiInsightResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.PartnerAiInsightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ota.travi.constant.ApiEndpoints.PARTNER_AI_INSIGHTS;

@RestController
@PreAuthorize("hasRole('DOI_TAC')")
public class PartnerAiController {
    private final PartnerAiInsightService partnerAiInsightService;

    public PartnerAiController(PartnerAiInsightService partnerAiInsightService) {
        this.partnerAiInsightService = partnerAiInsightService;
    }

    @GetMapping(PARTNER_AI_INSIGHTS)
    public ResponseEntity<?> getPartnerAiInsights(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "days", required = false) Integer days
    ) {
        try {
            PartnerAiInsightResponse response = partnerAiInsightService.getInsights(
                    userDetails.getUser().getId(),
                    days
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Loi khi phan tich AI partner: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
