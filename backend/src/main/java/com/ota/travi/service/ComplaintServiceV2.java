package com.ota.travi.service;

import com.ota.travi.dto.request.ComplaintCreateRequest;
import com.ota.travi.dto.request.ComplaintMessageCreateRequest;
import com.ota.travi.dto.request.ComplaintStatusUpdateRequest;
import com.ota.travi.dto.response.ComplaintResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service Module 5 (v2): xử lý ticket khiếu nại + tin nhắn.
 */
public interface ComplaintServiceV2 {

    ComplaintResponse createComplaint(String customerId, ComplaintCreateRequest request, List<MultipartFile> files);

    ComplaintResponse closeComplaintByCustomer(String customerId, String complaintId);

    ComplaintResponse postCustomerMessage(String customerId, String complaintId, ComplaintMessageCreateRequest request, List<MultipartFile> files);

    ComplaintResponse postPartnerMessage(String partnerId, String complaintId, ComplaintMessageCreateRequest request, List<MultipartFile> files);

    ComplaintResponse updateComplaintStatusByPartner(String partnerId, String complaintId, ComplaintStatusUpdateRequest request);

    Page<ComplaintResponse> getCustomerComplaints(String customerId, String status, String mucDo, Pageable pageable);

    Page<ComplaintResponse> getPartnerComplaints(String partnerId, String status, String mucDo, Pageable pageable);

    ComplaintResponse getComplaintDetailForCustomer(String customerId, String complaintId);

    ComplaintResponse getComplaintDetailForPartner(String partnerId, String complaintId);
}
