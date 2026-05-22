package com.ota.travi.controller;

import com.ota.travi.constant.ApiEndpoints;
import com.ota.travi.entity.DanhGia;
import com.ota.travi.service.AdminFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AdminFeedbackController {

    @Autowired
    private AdminFeedbackService adminFeedbackService;

    // Quản lý Đánh giá rác (Ẩn/Xóa)
    @PutMapping(ApiEndpoints.ADMIN_REVIEW_MODERATE)
    public ResponseEntity<?> moderateReview(@PathVariable String id, @RequestParam String action) {
        DanhGia updatedReview = adminFeedbackService.moderateReview(id, action);
        return ResponseEntity.ok(updatedReview);
    }

    // Điều tra & Đổi trạng thái Tố cáo
    @PutMapping(ApiEndpoints.ADMIN_REPORT_STATUS)
    public ResponseEntity<?> changeReportStatus(@PathVariable String id, @RequestParam String targetStatus) {
        Map<String, Object> result = adminFeedbackService.changeReportStatus(id, targetStatus);
        return ResponseEntity.ok(result);
    }

    // Phán quyết Tố cáo
    @PostMapping(ApiEndpoints.ADMIN_REPORT_VERDICT)
    public ResponseEntity<?> postVerdict(@PathVariable String id, @RequestParam String mucDoViPham) {
        Map<String, Object> result = adminFeedbackService.postVerdict(id, mucDoViPham);
        return ResponseEntity.ok(result);
    }
}
