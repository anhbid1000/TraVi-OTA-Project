package com.ota.travi.controller;

import com.ota.travi.dto.request.ComplaintMessageCreateRequest;
import com.ota.travi.dto.request.ComplaintStatusUpdateRequest;
import com.ota.travi.dto.response.ComplaintResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.ComplaintServiceV2;
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

import static com.ota.travi.constant.ApiEndpoints.PARTNER_PREFIX;

/**
 * Controller Module 5: API complaint cho đối tác.
 * Endpoint: /api/v1/partner/complaints
 */
@RestController
@RequestMapping(PARTNER_PREFIX + "/complaints")
public class PartnerComplaintController {

    @Autowired
    private ComplaintServiceV2 complaintService;

    // --- 1. API LẤY DANH SÁCH COMPLAINT CỦA PARTNER ---
    @GetMapping
    public ResponseEntity<?> getMyComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String mucDo,
            @RequestParam(defaultValue = "updated_desc") String sort
    ) {
        try {
            // 1. Lấy partnerId từ SecurityContext
            String partnerId = getCurrentUserId();

            // 2. Xử lý sort
            Sort sortOrder;
            if (sort.equals("newest")) {
                sortOrder = Sort.by("createdAt").descending();
            } else if (sort.equals("oldest")) {
                sortOrder = Sort.by("createdAt").ascending();
            } else {
                sortOrder = Sort.by("updatedAt").descending(); // updated_desc
            }

            // 3. Validate pagination
            if (size > 20) size = 20;
            Pageable pageable = PageRequest.of(page, size, sortOrder);

            // 4. Gọi service lấy danh sách complaint
            Page<ComplaintResponse> complaints = complaintService.getPartnerComplaints(partnerId, status, mucDo, pageable);
            return new ResponseEntity<>(complaints, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 2. API LẤY CHI TIẾT COMPLAINT ---
    @GetMapping("/{complaintId}")
    public ResponseEntity<?> getComplaintDetail(@PathVariable String complaintId) {
        try {
            // 1. Lấy partnerId từ SecurityContext
            String partnerId = getCurrentUserId();

            // 2. Gọi service lấy chi tiết complaint
            ComplaintResponse response = complaintService.getComplaintDetailForPartner(partnerId, complaintId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 3. API GỬI TIN NHẮN ---
    @PostMapping("/{complaintId}/messages")
    public ResponseEntity<?> postMessage(
            @PathVariable String complaintId,
            @Valid @RequestBody ComplaintMessageCreateRequest request
    ) {
        try {
            // 1. Lấy partnerId từ SecurityContext
            String partnerId = getCurrentUserId();

            // 2. Gọi service gửi tin nhắn
            ComplaintResponse response = complaintService.postPartnerMessage(partnerId, complaintId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 4. API CẬP NHẬT TRẠNG THÁI ---
    @PutMapping("/{complaintId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String complaintId,
            @Valid @RequestBody ComplaintStatusUpdateRequest request
    ) {
        try {
            // 1. Lấy partnerId từ SecurityContext
            String partnerId = getCurrentUserId();

            // 2. Gọi service cập nhật trạng thái
            ComplaintResponse response = complaintService.updateComplaintStatusByPartner(partnerId, complaintId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
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
