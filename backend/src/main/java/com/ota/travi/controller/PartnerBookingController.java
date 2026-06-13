package com.ota.travi.controller;

import com.ota.travi.dto.request.PartnerUpdateBookingStatusRequest;
import com.ota.travi.dto.response.PartnerBookingCompletionResponse;
import com.ota.travi.dto.response.PartnerBookingDetailResponse;
import com.ota.travi.dto.response.PartnerBookingListItemResponse;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.PartnerBookingLifecycleService;
import com.ota.travi.service.PartnerBookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static com.ota.travi.constant.ApiEndpoints.PARTNER_BOOKINGS;

@RestController
@RequestMapping(PARTNER_BOOKINGS)
@PreAuthorize("hasRole('DOI_TAC')")
public class PartnerBookingController {

    private final PartnerBookingService partnerBookingService;
    private final PartnerBookingLifecycleService partnerBookingLifecycleService;

    public PartnerBookingController(
            PartnerBookingService partnerBookingService,
            PartnerBookingLifecycleService partnerBookingLifecycleService
    ) {
        this.partnerBookingService = partnerBookingService;
        this.partnerBookingLifecycleService = partnerBookingLifecycleService;
    }

    @GetMapping
    public ResponseEntity<Page<PartnerBookingListItemResponse>> getPartnerBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "status", required = false) List<TrangThaiDon> statuses,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(name = "sort", required = false, defaultValue = "ngayTao,desc") String sort
    ) {
        Page<PartnerBookingListItemResponse> response = partnerBookingService.getPartnerBookings(
                userDetails.getUser().getId(),
                statuses,
                fromDate,
                toDate,
                keyword,
                page,
                size,
                sort
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<PartnerBookingDetailResponse> getPartnerBookingDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String bookingId
    ) {
        PartnerBookingDetailResponse response = partnerBookingService.getPartnerBookingDetail(
                userDetails.getUser().getId(),
                bookingId
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<PartnerBookingDetailResponse> updatePartnerBookingStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String bookingId,
            @Valid @RequestBody PartnerUpdateBookingStatusRequest request
    ) {
        PartnerBookingDetailResponse response = partnerBookingService.updateBookingStatus(
                userDetails.getUser().getId(),
                bookingId,
                request
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

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
