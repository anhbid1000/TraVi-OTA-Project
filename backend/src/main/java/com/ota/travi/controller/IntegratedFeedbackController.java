package com.ota.travi.controller;

import com.ota.travi.constant.ApiEndpoints;
import com.ota.travi.dto.request.CreateReviewRequest;
import com.ota.travi.entity.*;
import com.ota.travi.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class IntegratedFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // ==================== USER ENDPOINTS ====================

    @PostMapping(ApiEndpoints.USER_FEEDBACK_REVIEWS)
    public ResponseEntity<?> createReview(@Valid @RequestBody CreateReviewRequest dto) {
        feedbackService.createReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tạo đánh giá thành công!");
    }

    @PostMapping(ApiEndpoints.USER_FEEDBACK_COMPLAINTS)
    public ResponseEntity<?> createComplaint(@RequestBody KhieuNai complaint) {
        KhieuNai saved = feedbackService.createComplaint(complaint);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping(ApiEndpoints.USER_FEEDBACK_REPORTS)
    public ResponseEntity<?> createReport(@RequestBody ToCao report) {
        ToCao saved = feedbackService.createReport(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ==================== PARTNER ENDPOINTS ====================

    @GetMapping(ApiEndpoints.PARTNER_REVIEWS)
    public ResponseEntity<?> getPartnerReviews(
            @RequestParam(required = false) Integer soSao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "ngayTao")
        );

        Page<DanhGia> result = feedbackService.getPartnerReviews(soSao, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping(ApiEndpoints.PARTNER_REVIEW_REPLY)
    public ResponseEntity<?> replyReview(@PathVariable String id, @RequestBody PhanHoiPartner reply) {
        DanhGia updatedReview = feedbackService.replyReview(id, reply);
        return ResponseEntity.ok(updatedReview);
    }

    @PutMapping(ApiEndpoints.PARTNER_COMPLAINT_RESOLVE)
    public ResponseEntity<?> resolveComplaint(@PathVariable String id, @RequestParam String giaiQuyetOption) {
        KhieuNai updatedComplaint = feedbackService.resolveComplaint(id, giaiQuyetOption);
        return ResponseEntity.ok(updatedComplaint);
    }

    @PostMapping(value = ApiEndpoints.PARTNER_REPORT_EXPLANATION, consumes = {"multipart/form-data"})
    public ResponseEntity<?> submitExplanation(
            @PathVariable String id,
            @RequestPart("data") LichSuGiaiTrinh logicGiaiTrinh,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        // Tiến hành truyền logicGiaiTrinh và files vào Service xử lý
        ToCao updatedReport = feedbackService.submitExplanation(id, logicGiaiTrinh, files);
        return ResponseEntity.ok(updatedReport);
    }
}
