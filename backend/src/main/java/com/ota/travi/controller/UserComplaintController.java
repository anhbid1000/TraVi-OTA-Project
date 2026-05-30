package com.ota.travi.controller;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.ota.travi.validation.ValidUploadFiles;
import com.ota.travi.dto.request.ComplaintCreateRequest;
import com.ota.travi.dto.request.ComplaintMessageCreateRequest;
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

import static com.ota.travi.constant.ApiEndpoints.USER_PREFIX;

/**
 * Controller Module 5: API complaint cho khách hàng.
 * Endpoint: /api/v1/user/complaints
 */
@RestController
@RequestMapping(USER_PREFIX + "/complaints")
public class UserComplaintController {

    @Autowired
    private ComplaintServiceV2 complaintService;

    // --- 1. API TẠO COMPLAINT ---
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> createComplaint(
            @Valid @RequestPart("request") ComplaintCreateRequest request,
            @ValidUploadFiles(maxFiles = 10, allowPdf = true)
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            // 1. Lấy customerId từ SecurityContext
            String customerId = getCurrentUserId();

            // 2. Gọi service tạo complaint
            ComplaintResponse response = complaintService.createComplaint(customerId, request, files);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 2. API LẤY DANH SÁCH COMPLAINT CỦA KHÁCH ---
    @GetMapping
    public ResponseEntity<?> getMyComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String mucDo,
            @RequestParam(defaultValue = "updated_desc") String sort
    ) {
        try {
            // 1. Lấy customerId từ SecurityContext
            String customerId = getCurrentUserId();

            // 2. Xử lý sort - whitelist validation
            Sort sortOrder;
            switch (sort) {
                case "newest" -> sortOrder = Sort.by("createdAt").descending();
                case "oldest" -> sortOrder = Sort.by("createdAt").ascending();
                case "updated_desc" -> sortOrder = Sort.by("updatedAt").descending();
                default -> throw new IllegalArgumentException("Sort parameter phải là: newest, oldest, hoặc updated_desc");
            }

            // 3. Validate pagination
            if (size > 20) size = 20;
            Pageable pageable = PageRequest.of(page, size, sortOrder);

            // 4. Gọi service lấy danh sách complaint
            Page<ComplaintResponse> complaints = complaintService.getCustomerComplaints(customerId, status, mucDo, pageable);
            return new ResponseEntity<>(complaints, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 3. API LẤY CHI TIẾT COMPLAINT ---
    @GetMapping("/{complaintId}")
    public ResponseEntity<?> getComplaintDetail(@PathVariable String complaintId) {
        try {
            // 1. Lấy customerId từ SecurityContext
            String customerId = getCurrentUserId();

            // 2. Gọi service lấy chi tiết complaint
            ComplaintResponse response = complaintService.getComplaintDetailForCustomer(customerId, complaintId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 4. API GỬI TIN NHẮN ---
    @PostMapping(value = "/{complaintId}/messages", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> postMessage(
            @PathVariable String complaintId,
            @Valid @RequestPart("request") ComplaintMessageCreateRequest request,
            @ValidUploadFiles(maxFiles = 5, allowPdf = true)
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            // 1. Lấy customerId từ SecurityContext
            String customerId = getCurrentUserId();

            // 2. Gọi service gửi tin nhắn
            ComplaintResponse response = complaintService.postCustomerMessage(customerId, complaintId, request, files);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 5. API ĐÓNG COMPLAINT ---
    @PutMapping("/{complaintId}/close")
    public ResponseEntity<?> closeComplaint(@PathVariable String complaintId) {
        try {
            // 1. Lấy customerId từ SecurityContext
            String customerId = getCurrentUserId();

            // 2. Gọi service đóng complaint
            ComplaintResponse response = complaintService.closeComplaintByCustomer(customerId, complaintId);
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
