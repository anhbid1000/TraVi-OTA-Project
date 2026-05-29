package com.ota.travi.controller;

import com.ota.travi.dto.request.ReviewCreateRequest;
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

import static com.ota.travi.constant.ApiEndpoints.USER_PREFIX;

/**
 * Controller Module 5: API review cho khách hàng.
 * Endpoint: /api/v1/user/reviews
 */
@RestController
@RequestMapping(USER_PREFIX + "/reviews")
public class UserReviewController {

    @Autowired
    private ReviewServiceV2 reviewService;

    // --- 1. API TẠO REVIEW ---
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewCreateRequest request) {
        try {
            // 1. Lấy customerId từ SecurityContext
            String customerId = getCurrentUserId();

            // 2. Gọi service tạo review
            ReviewResponse response = reviewService.createReview(customerId, request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 2. API LẤY DANH SÁCH REVIEW CỦA KHÁCH ---
    @GetMapping
    public ResponseEntity<?> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        try {
            // 1. Lấy customerId từ SecurityContext
            String customerId = getCurrentUserId();

            // 2. Xử lý sort
            Sort sortOrder = sort.equals("oldest") 
                    ? Sort.by("createdAt").ascending() 
                    : Sort.by("createdAt").descending();

            // 3. Validate pagination
            if (size > 20) size = 20;
            Pageable pageable = PageRequest.of(page, size, sortOrder);

            // 4. Gọi service lấy danh sách review
            Page<ReviewResponse> reviews = reviewService.getCustomerReviews(customerId, pageable);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
