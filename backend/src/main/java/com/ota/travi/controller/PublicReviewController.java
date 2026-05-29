package com.ota.travi.controller;

import com.ota.travi.dto.response.ReviewResponse;
import com.ota.travi.service.ReviewServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ota.travi.constant.ApiEndpoints.PUBLIC_PREFIX;

/**
 * Controller Module 5: API review công khai.
 * Endpoint: /api/v1/public/business-profiles/{id}/reviews
 */
@RestController
@RequestMapping(PUBLIC_PREFIX + "/business-profiles")
public class PublicReviewController {

    @Autowired
    private ReviewServiceV2 reviewService;

    // --- 1. API LẤY REVIEW CÔNG KHAI CỦA CƠ SỞ ---
    @GetMapping("/{businessProfileId}/reviews")
    public ResponseEntity<?> getPublicReviews(
            @PathVariable String businessProfileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        try {
            // 1. Xử lý sort
            Sort sortOrder;
            switch (sort) {
                case "oldest" -> sortOrder = Sort.by("createdAt").ascending();
                case "rating_desc" -> sortOrder = Sort.by("soSao").descending();
                case "rating_asc" -> sortOrder = Sort.by("soSao").ascending();
                default -> sortOrder = Sort.by("createdAt").descending(); // newest
            }

            // 2. Validate pagination
            if (size > 20) size = 20;
            Pageable pageable = PageRequest.of(page, size, sortOrder);

            // 3. Gọi service lấy review công khai
            Page<ReviewResponse> reviews = reviewService.getPublicReviews(businessProfileId, pageable);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
