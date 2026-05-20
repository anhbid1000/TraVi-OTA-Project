package com.ota.travi.controller;

import com.ota.travi.dto.request.ApprovalStatusRequest;
import com.ota.travi.dto.response.AdminApprovalResponse;
import com.ota.travi.service.AdminApprovalService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ota.travi.constant.ApiEndpoints.ADMIN_APPROVALS;

@RestController
@PreAuthorize("hasRole('QUAN_TRI_VIEN')")
public class AdminApprovalController {
    @Autowired
    private AdminApprovalService adminApprovalService;

    @GetMapping(ADMIN_APPROVALS + "/pending")
    public ResponseEntity<?> getPendingApprovals() {
        List<AdminApprovalResponse> response = adminApprovalService.getPendingApprovals();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(ADMIN_APPROVALS + "/{id}")
    public ResponseEntity<?> getApprovalDetail(@PathVariable String id) {
        AdminApprovalResponse response = adminApprovalService.getApprovalDetail(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(ADMIN_APPROVALS + "/{id}/status")
    public ResponseEntity<?> updateApprovalStatus(
            @PathVariable String id,
            @Valid @RequestBody ApprovalStatusRequest request
    ) {
        AdminApprovalResponse response = adminApprovalService.updateApprovalStatus(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
