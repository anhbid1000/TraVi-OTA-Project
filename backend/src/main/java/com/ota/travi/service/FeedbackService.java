package com.ota.travi.service;

import com.ota.travi.dto.request.CreateReviewRequest;
import com.ota.travi.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FeedbackService {
    // User
    void createReview(CreateReviewRequest dto);
    KhieuNai createComplaint(KhieuNai complaint);
    ToCao createReport(ToCao report);

    // Partner
    Page<DanhGia> getPartnerReviews(Integer soSao, Pageable pageable);
    DanhGia replyReview(String reviewId, PhanHoiPartner reply);
    KhieuNai resolveComplaint(String complaintId, String giaiQuyetOption);
    ToCao submitExplanation(String reportId, LichSuGiaiTrinh logicGiaiTrinh, List<MultipartFile> files);
}
