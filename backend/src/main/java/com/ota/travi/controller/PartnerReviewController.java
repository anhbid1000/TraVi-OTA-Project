package com.ota.travi.controller;

import com.ota.travi.dto.request.PartnerReviewReplyRequest;
import com.ota.travi.dto.response.ReviewResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.ReviewServiceV2;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.ota.travi.constant.ApiEndpoints.PARTNER_REVIEWS;

/**
 * Controller Module 5: API review reply cho đối tác.
 * Endpoint: /api/v1/partner/reviews/{reviewId}/reply
 */
@RestController
@RequestMapping(PARTNER_REVIEWS)
public class PartnerReviewController {

    @Autowired
    private ReviewServiceV2 reviewService;

    // --- 1. API LẤY DANH SÁCH REVIEW CỦA PARTNER ---
    @GetMapping
    public ResponseEntity<?> getPartnerReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        try {
            String partnerId = getCurrentUserId();

            Sort sortOrder = "oldest".equalsIgnoreCase(sort)
                    ? Sort.by("createdAt").ascending()
                    : Sort.by("createdAt").descending();

            if (size > 20) size = 20;
            Pageable pageable = PageRequest.of(page, size, sortOrder);

            Page<ReviewResponse> reviews = reviewService.getPartnerReviews(partnerId, pageable);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 2. API TẠO/CẬP NHẬT REPLY ---
    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<?> createOrUpdateReply(
            @PathVariable String reviewId,
            @Valid @RequestBody PartnerReviewReplyRequest request
    ) {
        try {
            // 1. Lấy partnerId từ SecurityContext
            String partnerId = getCurrentUserId();

            // 2. Gọi service upsert reply
            ReviewResponse response = reviewService.upsertPartnerReply(partnerId, reviewId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{reviewId}/reply")
    public ResponseEntity<?> updateReply(
            @PathVariable String reviewId,
            @Valid @RequestBody PartnerReviewReplyRequest request
    ) {
        // Upsert logic giống POST
        return createOrUpdateReply(reviewId, request);
    }

    // --- Private helper method ---
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("Không thể xác thực người dùng");
    }
}
