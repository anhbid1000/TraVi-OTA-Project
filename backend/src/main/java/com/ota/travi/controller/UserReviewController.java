package com.ota.travi.controller;

import com.ota.travi.dto.request.ReviewCreateRequest;
import com.ota.travi.dto.response.ReviewResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.ReviewServiceV2;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import com.ota.travi.validation.ValidUploadFiles;

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

    // API tạo review hỗ trợ đính kèm ảnh (multipart/form-data)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReviewMultipart(
            @Valid @RequestPart("request") ReviewCreateRequest request,
            @ValidUploadFiles(maxFiles = 5, allowPdf = false)
            @RequestPart(name = "files", required = false) List<MultipartFile> files
    ) {
        try {
            String customerId = getCurrentUserId();
            ReviewResponse response = reviewService.createReview(customerId, request, files);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // API tạo review không đính kèm file (giữ tương thích cũ)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createReviewJson(@Valid @RequestBody ReviewCreateRequest request) {
        try {
            String customerId = getCurrentUserId();
            ReviewResponse response = reviewService.createReview(customerId, request, null);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        try {
            String customerId = getCurrentUserId();
            Sort sortOrder = sort.equals("oldest") ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending();
            if (size > 20) size = 20;
            Pageable pageable = PageRequest.of(page, size, sortOrder);
            Page<ReviewResponse> reviews = reviewService.getCustomerReviews(customerId, pageable);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("Không thể xác thực người dùng");
    }
}
