package com.ota.travi.controller;

import com.ota.travi.dto.response.PartnerBookingCompletionResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.PartnerBookingLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ota.travi.constant.ApiEndpoints.PARTNER_BOOKINGS;

@RestController
@RequestMapping(PARTNER_BOOKINGS)
@PreAuthorize("hasRole('DOI_TAC')")
@RequiredArgsConstructor
public class PartnerBookingController {

    private final PartnerBookingLifecycleService partnerBookingLifecycleService;

    @PostMapping("/{bookingId}/complete")
    public ResponseEntity<PartnerBookingCompletionResponse> completeBooking(
            @PathVariable String bookingId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PartnerBookingCompletionResponse response = partnerBookingLifecycleService.completeBooking(
                userDetails.getUser().getId(),
                bookingId
        );
        return ResponseEntity.ok(response);
    }
}
