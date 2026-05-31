package com.ota.travi.controller;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.ota.travi.validation.ValidUploadFiles;
import com.ota.travi.dto.request.ComplaintMessageCreateRequest;
import com.ota.travi.dto.request.ResolutionActionCreateRequest;
import com.ota.travi.dto.request.ResolutionActionCompleteRequest;
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
            @RequestParam(required = false) String category,
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
            Page<ComplaintResponse> complaints = complaintService.getPartnerComplaints(partnerId, status, mucDo, category, pageable);
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
    @PostMapping(value = "/{complaintId}/messages", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> postMessage(
            @PathVariable String complaintId,
            @Valid @RequestPart("request") ComplaintMessageCreateRequest request,
            @ValidUploadFiles(maxFiles = 5, allowPdf = true)
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            // 1. Lấy partnerId từ SecurityContext
            String partnerId = getCurrentUserId();

            // 2. Gọi service gửi tin nhắn
            ComplaintResponse response = complaintService.postPartnerMessage(partnerId, complaintId, request, files);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // --- 5. API TẠO PHƯƠNG ÁN XỬ LÝ ---
    @PostMapping("/{complaintId}/resolution-actions")
    public ResponseEntity<?> createResolutionAction(
            @PathVariable String complaintId,
            @Valid @RequestBody ResolutionActionCreateRequest request
    ) {
        try {
            String partnerId = getCurrentUserId();
            ComplaintResponse response = complaintService.createResolutionAction(partnerId, complaintId, request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 6. API BẮT ĐẦU THỰC HIỆN PHƯƠNG ÁN ---
    @PutMapping("/{complaintId}/resolution-actions/{actionId}/start")
    public ResponseEntity<?> startResolutionAction(
            @PathVariable String complaintId,
            @PathVariable String actionId
    ) {
        try {
            String partnerId = getCurrentUserId();
            ComplaintResponse response = complaintService.startResolutionAction(partnerId, complaintId, actionId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 7. API HOÀN TẤT PHƯƠNG ÁN ---
    @PutMapping(value = "/{complaintId}/resolution-actions/{actionId}/complete", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> completeResolutionAction(
            @PathVariable String complaintId,
            @PathVariable String actionId,
            @Valid @RequestPart("request") ResolutionActionCompleteRequest request,
            @ValidUploadFiles(maxFiles = 5, allowPdf = true)
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            String partnerId = getCurrentUserId();
            ComplaintResponse response = complaintService.completeResolutionAction(partnerId, complaintId, actionId, request, files);
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
